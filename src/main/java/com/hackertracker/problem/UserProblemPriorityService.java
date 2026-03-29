package com.hackertracker.problem;

import com.hackertracker.dao.*;
import com.hackertracker.schedule.PriorityCalculatorOptimized;
import com.hackertracker.schedule.ProcessProblemPriorityService;
import com.hackertracker.topic.Topic;
import com.hackertracker.user.User;
import com.hackertracker.user.UserProblemAttempt;
import com.hackertracker.user.UserProblemPriority;
import com.hackertracker.user.UserTopics;
import org.hibernate.Hibernate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for managing user problem priorities
 */
@Service
public class UserProblemPriorityService {

    private final UserProblemPriorityDAO priorityDao;
    private final ProblemDAO problemDao;
    private final UserDAO userDao;
    private final TopicDAO topicDao;
    private final UserTopicsDAO userTopicsDao;
    private final ProblemHistoryDAO problemHistoryDao;
    private final ProcessProblemPriorityService processProblemPriorityService;
    private final PriorityCalculatorOptimized priorityCalculatorOptimized;
    private final Map<Integer, Deque<String>> userRecentDifficulties = new ConcurrentHashMap<>();
    // private final UserProblemService userProblemService;

    public UserProblemPriorityService(
            UserProblemPriorityDAO priorityDao,
            PriorityCalculatorOptimized priorityCalculatorOptimized,
            ProblemDAO problemDao, UserDAO userDAO,
            TopicDAO topicDao,
            UserTopicsDAO userTopicsDao,
            ProblemHistoryDAO problemHistoryDao,
            ProcessProblemPriorityService processProblemPriorityService) {
        this.priorityDao = priorityDao;
        this.priorityCalculatorOptimized = priorityCalculatorOptimized;
        this.problemDao = problemDao;
        this.userDao = userDAO;
        this.topicDao = topicDao;
        this.userTopicsDao = userTopicsDao;
        this.problemHistoryDao = problemHistoryDao;
        this.processProblemPriorityService = processProblemPriorityService;
    }

    /**
     * Initialize priority for a new problem for a user
     */
    @Transactional
    public void initializePriorities(User user) {
        User myUser = userDao.getUserByIdWithCollections(user.getUserId());
        int pageNumber = 0;
        int batchSize = 100;

        // Pre-load topic ranks once
        List<Byte> userTopicRanks = myUser.getTopicRanks().getTopics();

        List<Problem> problemsBatch;

        do {
            problemsBatch = problemDao.getProblemsWithCollectionsPage(pageNumber, batchSize);
            List<UserProblemPriority> initialPriorities = new ArrayList<>();

            for (Problem problem : problemsBatch) {
                // Use optimized calculator for initial score
                double initialScore = priorityCalculatorOptimized.calculateInitialPriorityScoreOptimized(
                        problem,
                        Collections.emptyList(), // No attempts yet for new user
                        userTopicRanks);

                // Create new priority record
                UserProblemPriority priority = new UserProblemPriority();
                priority.setProblem(problem);
                priority.setUser(user);
                priority.setPriorityScore(initialScore);
                priority.setLastCalculation(LocalDateTime.now(ZoneOffset.UTC));
                initialPriorities.add(priority);
            }

            priorityDao.saveBatch(initialPriorities, batchSize);
            pageNumber++;
        } while (!problemsBatch.isEmpty());
    }

    /**
     * Update priority score after a user attempts a problem
     */
    @Transactional
    public void updatePriorityAfterAttempt(UserProblemAttempt attempt) {
        Problem problem = attempt.getProblem();
        User user = attempt.getUser();

        User myUser = userDao.getUserByIdWithCollections(user.getUserId());
        Problem myProblem = problemDao.getProblemByIdWithCollections(problem.getProblemId());

        if (myProblem.getProblemTopics() == null || !Hibernate.isInitialized(myProblem.getProblemTopics())) {
            throw new IllegalStateException("Problem topics not initialized!");
        }
        if (myUser.getProblemPriorities() == null || !Hibernate.isInitialized(myUser.getProblemPriorities())) {
            throw new IllegalStateException("User priorities not initialized!");
        }

        // Get or create priority record
        UserProblemPriority priority = priorityDao.findByProblemAndUser(myProblem, myUser);

        if (priority == null) {
            priority = new UserProblemPriority(myProblem, myUser, 0);
        }

        // Update last attempted timestamp
        priority.setLastAttempted(attempt.getEndTime());

        // Prepare optimized data
        List<Byte> userTopicRanks = myUser.getTopicRanks().getTopics();
        List<UserProblemAttempt> problemAttempts = myUser.getProblemAttempts().stream()
                .filter(a -> a.getProblem().getProblemId() == myProblem.getProblemId())
                .collect(Collectors.toList());

        // Add the new attempt to the list for calculation
        problemAttempts.add(attempt);

        // Recalculate priority score using optimized calculator
        double newScore = priorityCalculatorOptimized.calculatePriorityScoreOptimized(
                myProblem,
                problemAttempts,
                userTopicRanks);

        priority.setPriorityScore(newScore);
        priority.setLastCalculation(LocalDateTime.now(ZoneOffset.UTC));

        priorityDao.update(priority);
    }

    @Transactional
    public double recalculateSinglePriority(Problem problem, User user) {
        User myUser = userDao.getUserByIdWithCollections(user.getUserId());
        Problem myProblem = problemDao.getProblemByIdWithCollections(problem.getProblemId());

        if (myProblem.getProblemTopics() == null || !Hibernate.isInitialized(myProblem.getProblemTopics())) {
            throw new IllegalStateException("Problem topics not initialized!");
        }
        if (myUser.getProblemPriorities() == null || !Hibernate.isInitialized(myUser.getProblemPriorities())) {
            throw new IllegalStateException("User priorities not initialized!");
        }

        // Prepare optimized data
        List<Byte> userTopicRanks = myUser.getTopicRanks().getTopics();
        List<UserProblemAttempt> problemAttempts = myUser.getProblemAttempts().stream()
                .filter(a -> a.getProblem().getProblemId() == myProblem.getProblemId())
                .collect(Collectors.toList());

        // Calculate new score using optimized calculator
        double newScore = priorityCalculatorOptimized.calculatePriorityScoreOptimized(
                myProblem,
                problemAttempts,
                userTopicRanks);

        UserProblemPriority priority = priorityDao.findByProblemAndUser(problem, user);

        if (priority == null) {
            priority = new UserProblemPriority(problem, user, newScore);
            priority.setPriorityScore(newScore);
            priority.setLastCalculation(LocalDateTime.now(ZoneOffset.UTC));
            priorityDao.save(priority);
        } else {
            priority.setPriorityScore(newScore);
            priority.setLastCalculation(LocalDateTime.now(ZoneOffset.UTC));
            priorityDao.update(priority);
        }

        return newScore;
    }

    @Transactional
    public void recalculateAllPrioritiesByUser(User user) {
        // Get user with all collections pre-loaded
        User myUser = userDao.getUserByIdWithCollections(user.getUserId());

        // Get all priorities for this user
        List<UserProblemPriority> allPriorities = priorityDao.findByUser(myUser);

        // ====== PRE-LOADING PHASE ======

        // 1. Pre-load topic ranks
        List<Byte> userTopicRanks = myUser.getTopicRanks().getTopics();

        // 2. Pre-load and organize user attempts by problem ID
        Map<Integer, List<UserProblemAttempt>> attemptsByProblemId = myUser.getProblemAttempts().stream()
                .collect(Collectors.groupingBy(attempt -> attempt.getProblem().getProblemId()));

        // 3. Get all problem IDs needed
        List<Integer> allProblemIds = allPriorities.stream()
                .map(p -> p.getProblem().getProblemId())
                .distinct() // Ensure no duplicates
                .collect(Collectors.toList());

        // 4. Load all problems with collections in batches
        Map<Integer, Problem> problemsMap = new HashMap<>();
        int batchSize = 100;

        for (int i = 0; i < allProblemIds.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, allProblemIds.size());
            List<Integer> batchProblemIds = allProblemIds.subList(i, endIndex);

            // Fetch problems with collections (topics, tags, etc.)
            List<Problem> batchProblems = problemDao.getProblemsWithCollectionsByIds(batchProblemIds);

            // Store in map for O(1) lookup
            for (Problem problem : batchProblems) {
                problemsMap.put(problem.getProblemId(), problem);
            }
        }

        // Create a reference timestamp for all calculations
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // ====== CALCULATION PHASE ======

        List<UserProblemPriority> updatedPriorities = new ArrayList<>();

        for (UserProblemPriority priority : allPriorities) {
            // Get problem from pre-loaded map
            Problem problem = problemsMap.get(priority.getProblem().getProblemId());

            // Skip if problem not found (shouldn't happen, but for safety)
            if (problem == null)
                continue;

            // Get attempts for this problem
            List<UserProblemAttempt> problemAttempts = attemptsByProblemId.getOrDefault(problem.getProblemId(),
                    Collections.emptyList());

            // Calculate new score with optimized method
            double newScore = priorityCalculatorOptimized.calculatePriorityScoreOptimized(
                    problem,
                    problemAttempts,
                    userTopicRanks);

            // Update priority score
            priority.setPriorityScore(newScore);
            priority.setLastCalculation(now);
            updatedPriorities.add(priority);

            // Process in batches
            if (updatedPriorities.size() >= batchSize) {
                priorityDao.updateBatch(updatedPriorities, batchSize);
                updatedPriorities.clear();
            }
        }

        // Process remaining items
        if (!updatedPriorities.isEmpty()) {
            priorityDao.updateBatch(updatedPriorities, batchSize);
        }

        // Normalize scores after recalculation
        processProblemPriorityService.normalizeScoresForUser(user);
    }

    /**
     * Get the next recommended problem for a user
     * 
     * @param user
     * @return The next recommended problem
     */
    @Transactional(readOnly = true)
    public Problem getNextRecommendedProblem(User user) {
        // Get or initialize recent difficulties
        Deque<String> recentDifficulties = userRecentDifficulties.computeIfAbsent(
                user.getUserId(), uid -> new LinkedList<>(problemHistoryDao.loadRecentDifficulties(uid)));

        // Determine what difficulty to recommend next
        String nextDifficulty = determineNextDifficulty(user.getUserId(), recentDifficulties);
        // System.out.println("\n\nREQUESTING "+ nextDifficulty + " problem \n\n");

        // Get the highest priority problem of that difficulty
        Problem problem = priorityDao.getHighestPriorityProblemOfDifficulty(user, nextDifficulty);

        // System.out.println("\n\nGOT " + problem.getDifficultyLevel() + " PROBLEM
        // \n\n");

        // If no problems of preferred difficulty, get any problem
        if (problem == null) {
            // System.out.println("FALLING BACK TO findNextChallengeByPriorityScoreDesc");
            problem = priorityDao.findNextChallengeByPriorityScoreDesc(user).getProblem();
        }

        // Record this problem in history
        if (problem != null) {
            // Update in-memory cache
            // System.out.println("\n\nADDING " + problem.getDifficultyLevel() + "
            // problem\n\n");
            // System.out.println(problem);
            recentDifficulties.addFirst(problem.getDifficultyLevel().toLowerCase());
            userRecentDifficulties.put(user.getUserId(), recentDifficulties);

            if (recentDifficulties.size() > 10) {
                recentDifficulties.removeLast();
            }

            ProblemHistory ph = new ProblemHistory(user, problem, LocalDateTime.now(ZoneOffset.UTC));
            // Persist to database (separate transaction)
            problemHistoryDao.saveProblemHistory(ph);
        }

        // System.out.println("\n\nRETURNING " + problem.getDifficultyLevel() + "
        // PROBLEM \n\n");

        return problem;
    }

    /**
     * Determine what difficulty level to show next
     */
    private String determineNextDifficulty(int userId, Deque<String> recentDifficulties) {
        if (recentDifficulties.isEmpty()) {
            return "Easy"; // Start with easy
        }

        // Last difficulty shown
        String lastDifficulty = recentDifficulties.getFirst();

        // After medium/hard, always show an easy
        if ("medium".equals(lastDifficulty) || "hard".equals(lastDifficulty)) {
            double spin = Math.random();
            if (spin < 0.18) {
                return "Hard";
            } else if (spin < 0.36) {
                return "Medium";
            } else {
                return "Easy";
            }
        }

        // Count consecutive easy problems
        int consecutiveEasy = 0;
        for (String diff : recentDifficulties) {
            if ("easy".equals(diff)) {
                consecutiveEasy++;
            } else {
                break;
            }
        }

        // After 2-3 consecutive easy problems, try medium or hard
        if (consecutiveEasy >= 3) {
            // System.out.println("DO WE EVEN GET IN HERE?!");
            // Choose medium more often than hard (2:1 ratio)
            double spin = Math.random();
            if (spin < 0.40) {
                return "Hard";
            } else if (spin < 0.80) {
                return "Medium";
            } else {
                return "Easy";
            }
        }

        // Default to easy
        return "Easy";
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void recalculateAllPriorities() {
        int batchSize = 100;

        // Get mappings efficiently
        List<Object[]> priorityMappings = priorityDao.findAllPriorityMappings();

        // Build maps and extract IDs
        Map<String, Integer> priorityIdMap = new HashMap<>();
        Set<Integer> problemIdSet = new HashSet<>();
        Set<Integer> userIdSet = new HashSet<>();

        for (Object[] mapping : priorityMappings) {
            Integer problemId = (Integer) mapping[0];
            Integer userId = (Integer) mapping[1];
            Integer priorityId = (Integer) mapping[2];

            priorityIdMap.put(problemId + ":" + userId, priorityId);
            problemIdSet.add(problemId);
            userIdSet.add(userId);
        }

        List<Integer> allProblemIds = new ArrayList<>(problemIdSet);
        List<Integer> allUserIds = new ArrayList<>(userIdSet);

        // Process problem batches
        for (int i = 0; i < allProblemIds.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, allProblemIds.size());
            List<Integer> batchProblemIds = allProblemIds.subList(i, endIndex);

            processProblemPriorityService.processProblemBatch(batchProblemIds, allUserIds, priorityIdMap, batchSize);
        }

        processProblemPriorityService.normalizeAllUserPriorities();
    }

    /**
     * Batch initialize priorities for a new user
     */
    @Transactional
    public void initializeAllPrioritiesForNewUser(User user) {

        UserTopics userTopics = new UserTopics();
        userTopics.setUser(user);

        List<Byte> myTopics = new ArrayList<>();
        for (Topic topic : topicDao.getAllTopics()) {
            myTopics.add(topic.getTopicRank());
        }

        userTopics.setTopics(myTopics);
        userTopicsDao.saveUserTopics(userTopics);

        user.setTopicRanks(userTopics);
        userDao.updateUser(user);

        initializePriorities(user);
    }

    public void skipQuestion(Problem problem, User user) {
        UserProblemPriority priority = priorityDao.findByProblemAndUser(problem, user);
        double currentScore = priority.getPriorityScore();
        // Reduce by 50% of current score
        priority.setPriorityScore(Math.max(0, currentScore * 0.5));
        priorityDao.update(priority);
    }

}