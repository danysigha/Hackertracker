package com.hackertracker.security.problem;

import com.hackertracker.security.schedule.PriorityCalculator;
import com.hackertracker.security.dao.*;
import com.hackertracker.security.topic.Topic;
import com.hackertracker.security.user.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Service for managing user problem priorities
 */
@Service
public class UserProblemPriorityService {

    private final UserProblemPriorityDAO priorityDao;
    private final PriorityCalculator priorityCalculator;
    private final ProblemDAO problemDao;
    private final UserDAO userDao;
    private final TopicDAO topicDao;
    private final UserTopicsDAO userTopicsDao;
    private final ProblemHistoryDAO problemHistoryDao;
    private final Map<Integer, LinkedList<String>> userRecentDifficulties = new ConcurrentHashMap<>();
//    private final UserProblemService userProblemService;


    public UserProblemPriorityService(
            UserProblemPriorityDAO priorityDao,
            PriorityCalculator priorityCalculator,
            ProblemDAO problemDao, UserDAO userDAO,
            TopicDAO topicDao,
            UserTopicsDAO userTopicsDao,
            ProblemHistoryDAO problemHistoryDao) {
        this.priorityDao = priorityDao;
        this.priorityCalculator = priorityCalculator;
        this.problemDao = problemDao;
        this.userDao = userDAO;
        this.topicDao = topicDao;
        this.userTopicsDao = userTopicsDao;
        this.problemHistoryDao = problemHistoryDao;
    }

    /**
     * Initialize priority for a new problem for a user
     */
    @Transactional
    public UserProblemPriority initializePriority(Problem problem, User user) {

        // Check if priority already exists
        UserProblemPriority existingPriority = priorityDao.findByProblemAndUser(problem, user);

        if (existingPriority != null) {
            return existingPriority;
        }

        // Calculate initial priority score
        double initialScore = priorityCalculator.calculateInitialPriorityScore(problem, user);

        // Create new priority record
        UserProblemPriority priority = new UserProblemPriority();
        priority.setProblem(problem);
        priority.setUser(user);
        priority.setPriorityScore(initialScore);
        priority.setLastCalculation(LocalDateTime.now(ZoneOffset.UTC));

        return priorityDao.save(priority);
    }


    /**
     * Update priority score after a user attempts a problem
     */
    @Transactional
    public void updatePriorityAfterAttempt(UserProblemAttempt attempt) {
        Problem problem = attempt.getProblem();
        User user = attempt.getUser();

        // Get or create priority record
        UserProblemPriority priority = priorityDao.findByProblemAndUser(problem, user);

        if(priority == null) {
            priority = new UserProblemPriority(problem, user, 0);
        }

        // Update last attempted timestamp
        priority.setLastAttempted(attempt.getEndTime());

        // Recalculate priority score
        double newScore = priorityCalculator.calculatePriorityScore(problem, user);
        priority.setPriorityScore(newScore);
        priority.setLastCalculation(LocalDateTime.now(ZoneOffset.UTC));

        priorityDao.update(priority);
    }


    @Transactional
    public double recalculateSinglePriority(Problem problem, User user) {

        double newScore = priorityCalculator.calculatePriorityScore(problem, user);

        UserProblemPriority priority = priorityDao.findByProblemAndUser(problem, user);

        if(priority == null) {
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

        List<UserProblemPriority> newPriorities = new ArrayList<>();

        List<UserProblemPriority> allPriorities = priorityDao.findByUser(user);

        for (UserProblemPriority priority : allPriorities) {
            Problem problem = problemDao.getProblemByIdWithCollections(priority.getProblem().getProblemId());
            User myUser = userDao.getUserByIdWithCollections(priority.getUser().getUserId());

            // Recalculate score
            double newScore = priorityCalculator.calculatePriorityScore(problem, myUser);

            priority.setPriorityScore(newScore);
            priority.setLastCalculation(LocalDateTime.now(ZoneOffset.UTC));
            newPriorities.add(priority);
        }

        priorityDao.updateAll(newPriorities);

        // After recalculating all scores, normalize them to prevent inflation
        normalizeAllScores(allPriorities);
    }




    /**
     * Get prioritized problems for a user
     */
//    @Transactional(readOnly = true)
//    public Problem getNextTopPriorityProblemForUser(User user) {
//        UserProblemPriority priority = priorityDao.findNextChallengeByPriorityScoreDesc(user);
//        if (priority == null) {
//            return null;
//        }
//
//        int problemId = priority.getProblem().getProblemId();
//        return problemDao.getProblemByIdWithCollections(problemId);
//    }


    /**
     * Get the next recommended problem for a user
     * @param user
     * @return The next recommended problem
     */
    @Transactional(readOnly = true)
    public Problem getNextRecommendedProblem(User user) {
        // Get or initialize recent difficulties
        LinkedList<String> recentDifficulties = userRecentDifficulties.computeIfAbsent(
                user.getUserId(), problemHistoryDao::loadRecentDifficulties);

        // Determine what difficulty to recommend next
        String nextDifficulty = determineNextDifficulty(user.getUserId(), recentDifficulties);
//        System.out.println("\n\nREQUESTING "+ nextDifficulty + " problem \n\n");

        // Get the highest priority problem of that difficulty
        Problem problem = priorityDao.getHighestPriorityProblemOfDifficulty(user, nextDifficulty);

//        System.out.println("\n\nGOT " + problem.getDifficultyLevel() + " PROBLEM \n\n");

        // If no problems of preferred difficulty, get any problem
        if (problem == null) {
//            System.out.println("FALLING BACK TO findNextChallengeByPriorityScoreDesc");
            problem = priorityDao.findNextChallengeByPriorityScoreDesc(user).getProblem();
        }

        // Record this problem in history
        if (problem != null) {
            // Update in-memory cache
//            System.out.println("\n\nADDING " + problem.getDifficultyLevel() + " problem\n\n");
//            System.out.println(problem);
            recentDifficulties.addFirst(problem.getDifficultyLevel().toLowerCase());
            userRecentDifficulties.put(user.getUserId(), recentDifficulties);

            if (recentDifficulties.size() > 10) {
                recentDifficulties.removeLast();
            }

            ProblemHistory ph = new ProblemHistory(user, problem, LocalDateTime.now(ZoneOffset.UTC));
            // Persist to database (separate transaction)
            problemHistoryDao.saveProblemHistory(ph);
        }

//        System.out.println("\n\nRETURNING " + problem.getDifficultyLevel() + " PROBLEM \n\n");

        return problem;
    }


    /**
     * Determine what difficulty level to show next
     */
    private String determineNextDifficulty(int userId, LinkedList<String> recentDifficulties) {
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
            } else if(spin < 0.36) {
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
//            System.out.println("DO WE EVEN GET IN HERE?!");
            // Choose medium more often than hard (2:1 ratio)
            double spin = Math.random();
            if (spin < 0.40) {
                return "Hard";
            } else if(spin < 0.80) {
                return "Medium";
            } else {
                return "Easy";
            }
        }

        // Default to easy
        return "Easy";
    }


    /**
     * Scheduled job to recalculate all problem priorities for all users
     * Runs once daily
     */
    @Scheduled(cron = "0 0 0 * * ?")  // Run at midnight every day
    @Transactional
    public void recalculateAllPriorities() {

        List<UserProblemPriority> newPriorities = new ArrayList<>();

        List<UserProblemPriority> allPriorities = priorityDao.findAll();

        for (UserProblemPriority priority : allPriorities) {
            Problem problem = problemDao.getProblemByIdWithCollections(priority.getProblem().getProblemId());
            User user = userDao.getUserByIdWithCollections(priority.getUser().getUserId());

            // Recalculate score
            double newScore = priorityCalculator.calculatePriorityScore(problem, user);

            priority.setPriorityScore(newScore);
            priority.setLastCalculation(LocalDateTime.now(ZoneOffset.UTC));
            newPriorities.add(priority);
        }

        priorityDao.updateAll(newPriorities);

        // After recalculating all scores, normalize them to prevent inflation
        normalizeAllScores(allPriorities);
    }


    /**
     * Normalize all priority scores passed to ensure they stay within 0-100 range
     * This prevents score inflation over time
     * Run after recalculating priorities
     */
    @Transactional
    public void normalizeAllScores(List<UserProblemPriority> allPriorities) {
        // Group priorities by user ID to maintain relative importance within each user's set
        Map<Integer, List<UserProblemPriority>> prioritiesByUser = new HashMap<>();

        for (UserProblemPriority priority : allPriorities) {
            Integer userId = priority.getUser().getUserId();
            if (!prioritiesByUser.containsKey(userId)) {
                prioritiesByUser.put(userId, new ArrayList<>());
            }
            prioritiesByUser.get(userId).add(priority);
        }

        // Process each user's priorities separately
        for (List<UserProblemPriority> userPriorities : prioritiesByUser.values()) {
            if (userPriorities.size() <= 1) {
                continue; // Skip if only one problem for this user
            }

            // Find min and max scores for this user
            double minScore = Double.MAX_VALUE;
            double maxScore = Double.MIN_VALUE;

            for (UserProblemPriority priority : userPriorities) {
                double score = priority.getPriorityScore();
                if (score < minScore) minScore = score;
                if (score > maxScore) maxScore = score;
            }

            double range = maxScore - minScore;

            // Only normalize if there's an actual range and if max score exceeds 100
            if (range > 0 && maxScore > 100) {
                for (UserProblemPriority priority : userPriorities) {
                    double originalScore = priority.getPriorityScore();
                    // Normalize to 0-100 range
                    double normalizedScore = ((originalScore - minScore) / range) * 100;
                    priority.setPriorityScore(normalizedScore);
                }
            }
        }

        // Only call saveAll once with all updated priorities
        priorityDao.updateAll(allPriorities);
    }


    /**
     * Batch initialize priorities for a new user
     */
    @Transactional
    public void initializeAllPrioritiesForNewUser(User user) {

        //NEED TO THINK ABOUT THIS ONE

        UserTopics userTopics = new UserTopics();
        userTopics.setUser(user);

        List<Byte> myTopics = new ArrayList<>();
        for(Topic topic : topicDao.getAllTopics()) {
            myTopics.add(topic.getTopicRank());
        }

        userTopics.setTopics(myTopics);
        userTopicsDao.saveUserTopics(userTopics);

        user.setTopicRanks(userTopics);
        userDao.updateUser(user);

        for (Problem problem : problemDao.getAllProblems()) {
            initializePriority(problem, user);
        }
    }

}