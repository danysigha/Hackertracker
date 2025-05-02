package com.hackertracker.security.problem;

import com.hackertracker.security.Schedule.PriorityCalculator;
import com.hackertracker.security.dao.ProblemDAO;
import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.dao.UserProblemPriorityDAO;
import com.hackertracker.security.user.*;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * Service for managing user problem priorities
 */
@Service
public class UserProblemPriorityService {

    private final UserProblemPriorityDAO priorityDao;
    private final PriorityCalculator priorityCalculator;
    private final ProblemDAO problemDao;
    private final UserDAO userDao;
//    private final UserProblemService userProblemService;


    public UserProblemPriorityService(
            UserProblemPriorityDAO priorityDao,
            PriorityCalculator priorityCalculator,
            ProblemDAO problemDao, UserDAO userDAO) {
        this.priorityDao = priorityDao;
        this.priorityCalculator = priorityCalculator;
        this.problemDao = problemDao;
        this.userDao = userDAO;
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
        double initialScore = priorityCalculator.calculateInitialPriorityScore(problem);

        // Create new priority record
        UserProblemPriority priority = new UserProblemPriority();
        priority.setProblem(problem);
        priority.setUser(user);
        priority.setPriorityScore(initialScore);
        priority.setLastCalculation(new Date());

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
        priority.setLastCalculation(new Date());

        priorityDao.update(priority);
    }


    @Transactional
    public double recalculateSinglePriority(Problem problem, User user) {

        double newScore = priorityCalculator.calculatePriorityScore(problem, user);

        UserProblemPriority priority = priorityDao.findByProblemAndUser(problem, user);

        if(priority == null) {
            priority = new UserProblemPriority(problem, user, newScore);
            priority.setPriorityScore(newScore);
            priority.setLastCalculation(new Date());
            priorityDao.save(priority);
        } else {
            priority.setPriorityScore(newScore);
            priority.setLastCalculation(new Date());
            priorityDao.update(priority);
        }

        return newScore;
    }




    /**
     * Get prioritized problems for a user
     */
    @Transactional(readOnly = true)
    public Problem getNextTopPriorityProblemForUser(User user) {
        UserProblemPriority priority = priorityDao.findNextChallengeByPriorityScoreDesc(user);
        if (priority == null) {
            return null;
        }

        int problemId = priority.getProblem().getProblemId();
        return problemDao.getProblemByIdWithCollections(problemId);
    }
//    @Transactional(readOnly = true)
//    public Problem getNextTopPriorityProblemForUser(User user) {
//        // Return all user's problems ordered by priority score descending
//
//        return priorityDao.findNextChallengeByPriorityScoreDesc(user).getProblem();
//
//    }


    /**
     * Scheduled job to recalculate all problem priorities
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
            priority.setLastCalculation(new Date());
            newPriorities.add(priority);
        }

        priorityDao.updateAll(newPriorities);

        // After recalculating all scores, normalize them to prevent inflation
        normalizeAllScores(allPriorities);
    }


    /**
     * Normalize all priority scores for all users to ensure they stay within 0-100 range
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
        for (Problem problem : problemDao.getAllProblems()) {
            initializePriority(problem, user);
        }
    }

}