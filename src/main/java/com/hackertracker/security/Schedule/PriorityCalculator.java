package com.hackertracker.security.Schedule;

import com.hackertracker.security.dao.ProblemDAO;
import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.topic.Topic;
import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserProblemAttempt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.OptionalInt;

/**
 * Calculates priority scores for problems based on various factors:
 * - Topic rank (most important)
 * - Difficulty rating
 * - Time spent solving
 * - Recency of attempts
 */
@Component
public class PriorityCalculator {
    // Weight constants - adjust these to tune the formula
    private static final double TOPIC_RANK_WEIGHT = 0.5;     // 50% weight for topic rank
    private static final double DIFFICULTY_WEIGHT = 0.3;     // 30% weight for difficulty
    private static final double TIME_SPENT_WEIGHT = 0.08;    // 8% weight for time spent
    private static final double RECENCY_WEIGHT = 0.12;       // 12% weight for recency

    // Constants for normalization
    private static final int MAX_DIFFICULTY_RATING = 10;      // Assuming difficulty rating is 1-10
    private static final int MAX_TOPIC_RANK = 23;           // Assuming topic ranks are 1-23
    private static final long MAX_TIME_SPENT_MINUTES = 120;  // Cap time spent at 2 hours for scoring
    private static final long RECENCY_DAYS_MAX = 14;         // Consider attempts within last 14 days

    private final ProblemDAO problemDao;
    private final UserDAO userDao;

    public PriorityCalculator(ProblemDAO problemDao, UserDAO userDao) {
        this.problemDao = problemDao;
        this.userDao = userDao;
    }

    /**
     * Calculate priority score for a problem/user combination
     *
     * @param problem The problem
     * @param user The user
     * @return The calculated priority score (higher score = higher priority)
     */
    public double calculatePriorityScore(Problem problem, User user) {
        // Get the highest topic rank for this problem (most important)
        int topicRankScore = getTopicRankScore(problem, user);

        // Get the user's difficulty rating for this problem
        int difficultyScore = getDifficultyScore(problem, user);

        // Calculate time spent score based on previous attempts
        int timeSpentScore = getTimeSpentScore(problem, user);

        // Calculate recency score
        int recencyScore = getRecencyScore(problem, user);

        // Calculate weighted sum
        double weightedScore = (TOPIC_RANK_WEIGHT * topicRankScore) +
                (DIFFICULTY_WEIGHT * difficultyScore) +
                (TIME_SPENT_WEIGHT * timeSpentScore) +
                (RECENCY_WEIGHT * recencyScore);

        double uniqueFactor = generateUniqueFactor(problem);

        return Math.min(100, Math.max(0, weightedScore)) + (uniqueFactor / 100);

    }


    /**
     * Get score based on topic rank (higher rank = higher score)
     */
    private int getTopicRankScore(Problem problem, User user) {

        User myUser = userDao.getUserByIdWithTopics(user.getUserId());

//        Problem myProblem = problemDao.getProblemByIdWithCollections(problem.getProblemId());

        List<Byte> topicRanks = myUser.getTopicRanks().getTopics();

        OptionalInt highestRank = topicRanks.stream()
                .mapToInt(Byte::intValue)
                .max();

//        List<Topic> topics = myProblem.getListTopics();
//        if (topics.isEmpty()) {
//            return 50; // Default middle value if no topics
//        }

        // Find the highest rank among all topics associated with this problem
//        OptionalInt highestRank = topics.stream()
//                .mapToInt(Topic::getTopicRank)
//                .max();

        if (highestRank.isPresent()) {
            // INVERTED normalization - lower ranks get higher scores
            // This means rank 1 gets closer to 100, and MAX_TOPIC_RANK gets closer to 0
            return (int) (100 - (highestRank.getAsInt() * 100.0 / MAX_TOPIC_RANK));
        } else {
            return 50; // Default middle value
        }
    }


    /**
     * Get score based on difficulty rating
     * Higher user difficulty rating = higher priority (more challenging problems)
     */
    private int getDifficultyScore(Problem problem, User user) {

        User myUser = userDao.getUserByIdWithCollections(user.getUserId());

        List<UserProblemAttempt> attempts = myUser.getListAttempts().stream()
                .filter(attempt -> attempt.getProblem().getProblemId() == problem.getProblemId() )
                .toList();

        if (attempts.isEmpty()) {
            // If no attempts yet, use problem's difficulty level
            return convertDifficultyLevelToScore(problem.getDifficultyLevel());
        }

        // Use the most recent attempt's difficulty rating
        byte latestRating = attempts.stream()
                .max((a1, a2) -> a1.getEndTime().compareTo(a2.getEndTime()))
                .map(UserProblemAttempt::getDifficultyRating)
                .orElse((byte)5); // Default to medium difficulty

        // NEW CODE HERE: Look at the trend in difficulty ratings (if multiple attempts)
        if (attempts.size() > 1) {
            double avgRating = attempts.stream()
                    .mapToInt(a -> a.getDifficultyRating())
                    .average()
                    .orElse(5.0);

            // If latest rating is lower than average, further reduce score
            // (problem is getting easier for the user)
            if (latestRating < avgRating) {
                latestRating = (byte)Math.max(1, latestRating - 1);
            }
        }

        // Normalize to 0-100
        return (int) ((latestRating * 100.0) / MAX_DIFFICULTY_RATING);
    }


    /**
     * Convert difficulty level string to numeric score
     */
    private int convertDifficultyLevelToScore(String difficultyLevel) {
        return switch (difficultyLevel.toLowerCase()) {
            case "easy" -> 20;
            case "medium" -> 50;
            case "hard" -> 80;
            default -> 50; // Default to medium
        };
    }


    /**
     * Get score based on time spent on problem
     * More time spent = more challenging = higher priority
     */
    private int getTimeSpentScore(Problem problem, User user) {

        User myUser = userDao.getUserByIdWithCollections(user.getUserId());

        List<UserProblemAttempt> attempts = myUser.getListAttempts().stream()
                .filter(attempt -> attempt.getProblem().getProblemId() == problem.getProblemId())
                .toList();

        if (attempts.isEmpty()) {
            return 50; // Default middle value
        }

        // Calculate average time spent across all attempts
        long totalMinutes = 0;
        int attemptCount = 0;

        for (UserProblemAttempt attempt : attempts) {
            if (attempt.getStartTime() != null && attempt.getEndTime() != null) {
                // Calculate minutes between LocalDateTimes
                long minutes = ChronoUnit.MINUTES.between(attempt.getStartTime(), attempt.getEndTime());
                totalMinutes += minutes;
                attemptCount++;
            }
        }

        if (attemptCount == 0) {
            return 50; // Default if no valid attempts
        }

        long avgMinutes = totalMinutes / attemptCount;

        // Cap at maximum time and normalize to 0-100
        long capped = Math.min(avgMinutes, MAX_TIME_SPENT_MINUTES);
        return (int) ((capped * 100.0) / MAX_TIME_SPENT_MINUTES);
    }


    /**
     * Get score based on recency of attempts
     * More recent = lower priority (already practiced recently)
     * Older = higher priority (needs review)
     */
    private int getRecencyScore(Problem problem, User user) {

        User myUser = userDao.getUserByIdWithCollections(user.getUserId());

        List<UserProblemAttempt> attempts = myUser.getListAttempts().stream()
                .filter(attempt -> attempt.getProblem().getProblemId() == problem.getProblemId() )
                .toList();

        if (attempts.isEmpty()) {
            return 100; // Never attempted = highest priority
        }

        // Find latest attempt
        LocalDateTime latestAttempt = attempts.stream()
                .map(UserProblemAttempt::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (latestAttempt == null) {
            return 100; // No completed attempts = highest priority
        }

        // Calculate days since latest attempt
        long daysSince = ChronoUnit.DAYS.between(latestAttempt, LocalDateTime.now(ZoneOffset.UTC));

        // More attempts and shorter times may indicate mastery
        int attemptCount = attempts.size();
        long avgTimeMinutes = getAverageTimeInMinutes(attempts);

        // Completion penalty calculation using both attempt count and time
        int completionPenalty = 0;
        if (attemptCount > 1) {
            // Time factor: shorter time = more penalty (max 20)
            // Inverse relationship with time, capped at MAX_TIME_SPENT_MINUTES
            int timeFactor = (int)(20 * (1 - Math.min(avgTimeMinutes, MAX_TIME_SPENT_MINUTES) / MAX_TIME_SPENT_MINUTES));

            // Attempt factor: more attempts = more penalty (max 20)
            int attemptFactor = Math.min(attemptCount * 5, 20);

            // Combined penalty diminishes over time (14 days to fade)
            completionPenalty = (int)((timeFactor + attemptFactor) * Math.exp(-daysSince / 14.0));
        }

        // More days = higher score (capped at RECENCY_DAYS_MAX)
        long cappedDays = Math.min(daysSince, RECENCY_DAYS_MAX);
        return Math.max(0, (int)((cappedDays * 100.0) / RECENCY_DAYS_MAX) - completionPenalty);
    }

    // Helper method to calculate average time
    private long getAverageTimeInMinutes(List<UserProblemAttempt> attempts) {
        long totalMinutes = 0;
        int validAttempts = 0;

        for (UserProblemAttempt attempt : attempts) {
            if (attempt.getStartTime() != null && attempt.getEndTime() != null) {
                // Use ChronoUnit to calculate minutes between LocalDateTimes
                long minutes = ChronoUnit.MINUTES.between(attempt.getStartTime(), attempt.getEndTime());
                totalMinutes += minutes;
                validAttempts++;
            }
        }

        return validAttempts > 0 ? totalMinutes / validAttempts : 0;
    }


    /**
     * Calculate initial priority score when user first encounters a problem
     * This method uses problem metadata only (no attempt history)
     */
    public double calculateInitialPriorityScore(Problem problem, User user) {
        // Topic rank is the primary factor
        int topicRankScore = getTopicRankScore(problem, user);

        // Use problem's static difficulty level
        int difficultyScore = invertDifficultyScore(
                convertDifficultyLevelToScore(problem.getDifficultyLevel())
        );

        // Time spent and recency are maximized since user hasn't practiced this problem yet
        int timeSpentScore = 50;   // Default to middle value for time spent
        int recencyScore = 100;    // Maximum recency score (never attempted)

        // Calculate weighted sum
        double weightedScore = (TOPIC_RANK_WEIGHT * topicRankScore) +
                (DIFFICULTY_WEIGHT * difficultyScore) +
                (TIME_SPENT_WEIGHT * timeSpentScore) +
                (RECENCY_WEIGHT * recencyScore);

        double uniqueFactor = generateUniqueFactor(problem);

        return Math.min(100, Math.max(0, weightedScore)) + (uniqueFactor / 100);
    }


    /**
     * Inverts a difficulty score (0-100) so that easier problems get higher priority
     * @param originalScore The original difficulty score
     * @return The inverted score
     */
    private int invertDifficultyScore(int originalScore) {
        return 100 - originalScore;
    }


    /**
     * Generates a small unique factor (between 0 and 0.99) based on problem ID
     * to break potential ties in priority scores
     */
    private double generateUniqueFactor(Problem problem) {
        // Generate a value between 0 and 0.99 based on the problem's hashCode
        // This ensures the same problem always gets the same tie-breaking value
        int hashValue = problem.hashCode();
        if (hashValue < 0) {
            hashValue = -hashValue; // Ensure positive value
        }
        return (hashValue % 100) / 100.0;
    }

}
