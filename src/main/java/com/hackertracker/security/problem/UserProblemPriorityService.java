package com.hackertracker.security.problem;

import com.hackertracker.security.Schedule.PriorityCalculator;
import com.hackertracker.security.dao.ProblemDAO;
import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.dao.UserProblemPriorityDAO;
import com.hackertracker.security.dto.ProblemDTO;
import com.hackertracker.security.dto.UserDTO;
import com.hackertracker.security.dto.UserProblemPriorityDTO;
import com.hackertracker.security.dto.UserProblemService;
import com.hackertracker.security.user.*;
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
    private final UserProblemService userProblemService;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    public UserProblemPriorityService(
            UserProblemPriorityDAO priorityDao,
            PriorityCalculator priorityCalculator,
            ProblemDAO problemDao,
            UserDAO userDao,
            UserProblemService userProblemService) {
        this.priorityDao = priorityDao;
        this.priorityCalculator = priorityCalculator;
        this.problemDao = problemDao;
        this.userDao = userDao;
        this.userProblemService = userProblemService;
    }


    /**
     * Initialize priority for a new problem for a user
     */
    @Transactional
    public UserProblemPriority initializePriority(ProblemDTO problemDto, User user) {

        Problem problem = problemDao.getProblemById(problemDto.getProblemId());

        // Check if priority already exists
        UserProblemPriority existingPriority = priorityDao.findByProblemAndUser(problem, user);

        if (existingPriority != null) {
            return existingPriority;
        }

        // Calculate initial priority score
        double initialScore = priorityCalculator.calculateInitialPriorityScore(problemDto);

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

        ProblemDTO problemDto = userProblemService.getProblemDto(problem);

        UserDTO userDto = userProblemService.getUserDto(user);

        // Get or create priority record
        UserProblemPriority priority = priorityDao.findByProblemAndUser(problem, user);

        if(priority == null) {
            priority = new UserProblemPriority(problem, user, 0);
        }

        // Update last attempted timestamp
        priority.setLastAttempted(attempt.getEndTime());

        // Recalculate priority score
        double newScore = priorityCalculator.calculatePriorityScore(problemDto, userDto);
        priority.setPriorityScore(newScore);
        priority.setLastCalculation(new Date());

        priorityDao.save(priority);
    }


    @Transactional
    public double recalculateSinglePriority(ProblemDTO problemDto, User user) {

        Problem problem = problemDao.getProblemById(problemDto.getProblemId());

        UserDTO userDto = userProblemService.getUserDto(user);

        double newScore = priorityCalculator.calculatePriorityScore(problemDto, userDto);

        UserProblemPriority priority = priorityDao.findByProblemAndUser(problem, user);

        if(priority == null) {
            priority = new UserProblemPriority(problem, user, newScore);
        }

        priority.setPriorityScore(newScore);
        priority.setLastCalculation(new Date());
        priorityDao.save(priority);

        return newScore;
    }


    /**
     * Get prioritized problems for a user
     */
    @Transactional(readOnly = true)
    public List<ProblemDTO> getPrioritizedProblemsForUser(User user) {
        // Return all user's problems ordered by priority score descending

        return priorityDao.findByUserOrderByPriorityScoreDesc(user)
                .stream()
                .map(UserProblemPriorityDTO::getProblemDto)
                .toList();
    }


    /**
     * Scheduled job to recalculate all problem priorities
     * Runs once daily
     */
    @Scheduled(cron = "0 0 0 * * ?")  // Run at midnight every day
    @Transactional
    public void recalculateAllPriorities() {

        List<UserProblemPriorityDTO> allPrioritiesDto = priorityDao.findAll();
        List<UserProblemPriority> allPriorities = new ArrayList<>();

        for (UserProblemPriorityDTO priorityDto : allPrioritiesDto) {
            ProblemDTO problemDto = priorityDto.getProblemDto();
            UserDTO userDto = priorityDto.getUserDto();

            Problem problem = problemDao.getProblemById(problemDto.getProblemId());
            User user = userDao.getUserById(userDto.getUserId());
            UserProblemPriority priority = priorityDao.findByProblemAndUser(problem, user);

            // Recalculate score
            double newScore = priorityCalculator.calculatePriorityScore(problemDto, userDto);

            priority.setPriorityScore(newScore);
            priority.setLastCalculation(new Date());
            allPriorities.add(priority);
        }

        priorityDao.saveAll(allPriorities);

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
        priorityDao.saveAll(allPriorities);
    }


    /**
     * Batch initialize priorities for a new user
     */
    @Transactional
    public void initializeAllPrioritiesForNewUser(User user) {
        for (ProblemDTO problemDto : userProblemService.getAllProblemDtos()) {
            initializePriority(problemDto, user);
        }
    }

}