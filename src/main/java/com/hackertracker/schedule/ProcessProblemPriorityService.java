package com.hackertracker.schedule;

import com.hackertracker.dao.ProblemDAO;
import com.hackertracker.dao.UserDAO;
import com.hackertracker.dao.UserProblemPriorityDAO;
import com.hackertracker.problem.Problem;
import com.hackertracker.user.User;
import com.hackertracker.user.UserProblemAttempt;
import com.hackertracker.user.UserProblemPriority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcessProblemPriorityService {

    private final ProblemDAO problemDao;
    private final UserDAO userDao;
    private final UserProblemPriorityDAO priorityDao;
    private final PriorityCalculatorOptimized priorityCalculatorOptimized;

    ProcessProblemPriorityService(ProblemDAO problemDao, UserDAO userDao, UserProblemPriorityDAO priorityDao, PriorityCalculatorOptimized priorityCalculatorOptimized) {
        this.problemDao = problemDao;
        this.userDao = userDao;
        this.priorityCalculatorOptimized = priorityCalculatorOptimized;
        this.priorityDao = priorityDao;
    }

    @Transactional
    public void processProblemBatch(List<Integer> batchProblemIds, List<Integer> allUserIds,
                                    Map<String, Integer> priorityIdMap, int batchSize) {
        // Load problems for this batch
        List<Problem> problems = problemDao.getProblemsWithCollectionsByIds(batchProblemIds);

        // Process each batch of users
        for(int j = 0; j < allUserIds.size(); j += batchSize) {
            int userEndIndex = Math.min(j + batchSize, allUserIds.size());
            List<Integer> batchUserIds = allUserIds.subList(j, userEndIndex);
            List<User> users = userDao.getUsersWithCollectionsByIds(batchUserIds);

            List<UserProblemPriority> updatedPriorities = new ArrayList<>();

            for (User user : users) {
                // Pre-load topic ranks
                List<Byte> userTopicRanks = user.getTopicRanks().getTopics();

                // Pre-load and organize attempts by problem
                Map<Integer, List<UserProblemAttempt>> attemptsByProblemId =
                        user.getProblemAttempts().stream()
                                .collect(Collectors.groupingBy(a -> a.getProblem().getProblemId()));

                for (Problem problem : problems) {
                    // Check if this combination has a priority
                    String key = problem.getProblemId() + ":" + user.getUserId();
                    Integer priorityId = priorityIdMap.get(key);

                    if (priorityId != null) {
                        List<UserProblemAttempt> problemAttempts =
                                attemptsByProblemId.getOrDefault(problem.getProblemId(), Collections.emptyList());

                        UserProblemPriority priority = new UserProblemPriority();
                        priority.setPriorityId(priorityId);
                        priority.setProblem(problem);
                        priority.setUser(user);

                        // Use optimized calculator
                        double newScore = priorityCalculatorOptimized.calculatePriorityScoreOptimized(
                                problem,
                                problemAttempts,
                                userTopicRanks);

                        priority.setPriorityScore(newScore);
                        priority.setLastCalculation(LocalDateTime.now(ZoneOffset.UTC));

                        updatedPriorities.add(priority);

                        if (updatedPriorities.size() >= batchSize) {
                            priorityDao.updateBatch(updatedPriorities, batchSize);
                            updatedPriorities.clear();
                        }
                    }
                }
            }

            if (!updatedPriorities.isEmpty()) {
                priorityDao.updateBatch(updatedPriorities, batchSize);
            }
        }
    }


    /**
     * Normalize all priority scores passed to ensure they stay within 0-100 range
     * This prevents score inflation over time
     * Run after recalculating priorities
     */
    @Transactional
    public void normalizeScoresForUser(User user) {
        // Get min and max scores in one query
        Object[] minMaxScores = priorityDao.findMinMaxScoresByUserId(user.getUserId());

        if (minMaxScores == null || minMaxScores[0] == null || minMaxScores[1] == null) {
            return; // No priorities or insufficient data
        }

        double minScore = (Double) minMaxScores[0];
        double maxScore = (Double) minMaxScores[1];
        double range = maxScore - minScore;

        // Only normalize if necessary
        if (range > 0 && maxScore > 100) {
            // Use direct SQL update instead of loading and updating individual objects
            priorityDao.normalizeScoresByUserSql(user.getUserId(), minScore, range);
        }
    }


    @Transactional
    public void normalizeAllUserPriorities() {
        // Step 1: Get min and max scores for ALL users in one query
        List<Object[]> userMinMaxScores = priorityDao.findMinMaxScoresForAllUsers();

        // Step 2: Process in batches
        int batchSize = 100;

        for (Object[] userScores : userMinMaxScores) {
            Integer userId = (Integer) userScores[0];
            Double minScore = (Double) userScores[1];
            Double maxScore = (Double) userScores[2];
            Long count = (Long) userScores[3];

            // Skip users with no priorities or just one (nothing to normalize)
            if (count <= 1 || minScore.equals(maxScore)) {
                continue;
            }

            double range = maxScore - minScore;

            // Only normalize if there's a range and max exceeds 100
            if (range > 0 && maxScore > 100) {
                // Get all priority IDs and scores for this user
                List<Object[]> priorityData = priorityDao.findPriorityIdsAndScoresByUserId(userId);

                // Create a batch of mock priority objects
                List<UserProblemPriority> batchToUpdate = new ArrayList<>(batchSize);
                LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

                for (Object[] priority : priorityData) {
                    Integer priorityId = (Integer) priority[0];
                    Double score = (Double) priority[1];

                    // Calculate normalized score
                    double normalizedScore = ((score - minScore) / range) * 100;

                    // Only include if the change is significant
                    if (Math.abs(normalizedScore - score) > 0.001) {
                        // Create a minimal priority object with just the ID and score
                        UserProblemPriority mockPriority = new UserProblemPriority();
                        mockPriority.setPriorityId(priorityId);
                        mockPriority.setPriorityScore(normalizedScore);
                        mockPriority.setLastCalculation(now);

                        batchToUpdate.add(mockPriority);

                        // Update when batch is full
                        if (batchToUpdate.size() >= batchSize) {
                            priorityDao.updateBatch(batchToUpdate, batchSize);
                            batchToUpdate.clear();
                        }
                    }
                }

                // Update any remaining items
                if (!batchToUpdate.isEmpty()) {
                    priorityDao.updateBatch(batchToUpdate, batchSize);
                }
            }
        }
    }
}
