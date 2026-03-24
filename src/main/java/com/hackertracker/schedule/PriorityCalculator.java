package com.hackertracker.schedule;

import com.hackertracker.problem.Problem;
import com.hackertracker.topic.Topic;
import com.hackertracker.user.User;
import com.hackertracker.user.UserProblemAttempt;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

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

    private static final double DIFFICULTY_DECAY_RATE = 0.1; // Time decay parameter

    private static final double TOPIC_RANK_WEIGHT = 0.3;     // 20% weight for topic rank
    private static final double DIFFICULTY_WEIGHT = 0.2;     // 20% weight for difficulty
    private static final double RETENTION_WEIGHT = 0.3;      // 40% weight for retention
    private static final double TIME_SPENT_WEIGHT = 0.1;    // 10% weight for time spent
    private static final double RECENCY_WEIGHT = 0.1;       // 10% weight for recency

    private static final double EASY = 5.0;
    private static final double MEDIUM = 3.0;
    private static final double HARD = 1.0;

    // Constants for normalization
    private static final int MAX_DIFFICULTY_RATING = 10;      // Assuming difficulty rating is 1-10
    private static final int MAX_TOPIC_RANK = 23;           // Assuming topic ranks are 1-23
    private static final long MAX_TIME_SPENT_MINUTES = 120;  // Cap time spent at 2 hours for scoring
    private static final long RECENCY_DAYS_MAX = 14;         // Consider attempts within last 14 days

    private final Function<Integer, Random> randomProvider;


    public PriorityCalculator() {
        this.randomProvider = Random::new;
    }

    // Constructor for testing
    public PriorityCalculator(Function<Integer, Random> randomProvider) {
        this.randomProvider = randomProvider;
    }

    /**
     * Calculate priority score for a problem/user combination
     *
     * @param myProblem The problem
     * @param myUser The user
     * @return The calculated priority score (higher score = higher priority)
     */
    public double calculatePriorityScore(Problem myProblem, User myUser) {
        // Get the highest topic rank for this problem (most important)
        int topicRankScore = getTopicRankScore(myProblem, myUser);

        // Get the user's difficulty rating for this problem
        int difficultyScore = getDifficultyScore(myProblem, myUser);

        // Calculate time spent score based on previous attempts
        int timeSpentScore = getTimeSpentScore(myProblem, myUser);

        // Calculate recency score
        int recencyScore = getRecencyScore(myProblem, myUser);

        // Calculate weighted sum
        double weightedScore = (TOPIC_RANK_WEIGHT * topicRankScore) +
                (DIFFICULTY_WEIGHT * difficultyScore) +
                (TIME_SPENT_WEIGHT * timeSpentScore) +
                (RECENCY_WEIGHT * recencyScore) +
                (RETENTION_WEIGHT * (1 - getRetentionScore(myProblem, myUser)));

        double uniqueFactor = generateUniqueFactor(myProblem);

        return Math.min(100, Math.max(0, weightedScore)) + (uniqueFactor / 100);

    }


    /**
     * Get score based on topic rank (higher rank = higher score)
     */
     int getTopicRankScore(Problem myProblem, User myUser) {

        int highestRank = -1;

        List<Byte> userTopicRanks = myUser.getTopicRanks().getTopics();

        List<Topic> topics = myProblem.getListTopics();
        if (topics.isEmpty()) {
            return 50; // Default middle value if no topics
        }

        for (Topic topic : topics) {
            int currentRank = userTopicRanks.get(topic.getTopicId() - 1).intValue();
            if (highestRank == -1 || currentRank < highestRank) {
                highestRank = currentRank;
            }
        }

        if (highestRank > -1) {
            // INVERTED normalization - lower ranks get higher scores
            // This means rank 1 gets closer to 100, and MAX_TOPIC_RANK gets closer to 0
            return (int) (100 - (highestRank * 100.0 / MAX_TOPIC_RANK));
        } else {
            return 50; // Default middle value
        }
    }


    /**
     * Get score based on difficulty rating
     * Higher user difficulty rating = higher priority (more challenging problems)
     */
     int getDifficultyScore(Problem myProblem, User myUser) {

        List<UserProblemAttempt> attempts = myUser.getListAttempts().stream()
                .filter(attempt -> attempt.getProblem().getProblemId() == myProblem.getProblemId() )
                .toList();

        if (attempts.isEmpty()) {
            // If no attempts yet, use problem's difficulty level
            return convertDifficultyLevelToScore(myProblem.getDifficultyLevel());
        }


        // Use the most recent attempt's difficulty rating
        double latestRating = attempts.stream()
                .max((a1, a2) -> a1.getEndTime().compareTo(a2.getEndTime()))
                .map( attempt -> (double) attempt.getDifficultyRating()).orElse(5.0); // Default to medium difficulty

        // Calculate time-weighted average of difficulty ratings
        double sumOfWeightedRatings = 0.0;
        double sumOfWeights = 0.0;

        for (UserProblemAttempt attempt : attempts) {
            long daysAgo = ChronoUnit.DAYS.between(attempt.getEndTime(), LocalDateTime.now());
            double weight = Math.exp(-DIFFICULTY_DECAY_RATE * daysAgo);  // Higher weight for recent attempts

            sumOfWeightedRatings += attempt.getDifficultyRating() * weight;
            sumOfWeights += weight;
        }

        double weightedAvgRating = sumOfWeightedRatings / sumOfWeights;

        // If latest rating is lower than average, further reduce score
        // (problem is getting easier for the user)

        if (latestRating < weightedAvgRating) {
            if (attempts.size() > 3) {
                latestRating = Math.max(1, latestRating - 1.5);
            } else {
                latestRating = (byte)Math.max(1, latestRating - 1);
            }
        }

        // High variance might indicate inconsistent understanding
        double variance = calculateVariance(attempts);

        if (variance > 2.0) {
            // If ratings vary widely, slightly increase difficulty
            latestRating = Math.min(10, latestRating + 0.5);
        }

        // Only apply integration for users with few attempts (under 5)
        if (attempts.size() < 5) {
            int problemDifficulty = convertDifficultyLevelToScore(myProblem.getDifficultyLevel());
            double userPerception = ((latestRating * 100.0) / (double) MAX_DIFFICULTY_RATING);

            // Apply a weighted average that shifts toward user perception over time
            double objectiveWeight = Math.max(0, 1.0 - (attempts.size() * 0.2));
            double userWeight = 1.0 - objectiveWeight;

            return (int) ((problemDifficulty * objectiveWeight) + (userPerception * userWeight));
        }

        // For users with more attempts, trust their perception entirely
        return (int) ((latestRating * 100.0) / MAX_DIFFICULTY_RATING);
    }


    // Method to calculate variance of difficulty ratings
    double calculateVariance(List<UserProblemAttempt> attempts) {
        if (attempts.size() <= 1) {
            return 0.0; // No variance with 0 or 1 attempts
        }

        // Calculate mean
        double mean = attempts.stream()
                .mapToInt(UserProblemAttempt::getDifficultyRating)
                .average()
                .orElse(0.0);

        // Calculate sum of squared differences
        double sumSquaredDiff = attempts.stream()
                .mapToDouble(attempt -> {
                    double diff = attempt.getDifficultyRating() - mean;
                    return diff * diff;
                })
                .sum();

        // Divide by count (using n-1 for sample variance)
        return sumSquaredDiff / (attempts.size() - 1);
    }


    /**
     * Convert difficulty level string to numeric score
     */
    private int convertDifficultyLevelToScore(String difficultyLevel) {
        return switch (difficultyLevel.toLowerCase()) {
            case "easy" -> 65;    // Down from 80
            case "medium" -> 55;  // Down from 60
            case "hard" -> 45;    // Up from 40
            default -> 55;
        };
    }


    /**
     * Get score based on time spent on problem
     * More time spent = more challenging = higher priority
     */
    private int getTimeSpentScore(Problem myProblem, User myUser) {

        List<UserProblemAttempt> attempts = myUser.getListAttempts().stream()
                .filter(p -> p.getProblem().getProblemId() == myProblem.getProblemId()).toList();

        if (attempts.isEmpty()) {
            return 50; // Default middle value
        }

        // Calculate average time spent across all attempts
        double totalMinutes = 0;
        double sumOfWeights = 0;

        for (UserProblemAttempt attempt : attempts) {
            if (attempt.getStartTime() != null && attempt.getEndTime() != null) {
                long daysAgo = ChronoUnit.DAYS.between(attempt.getEndTime(), LocalDateTime.now());
                double weight = Math.exp(-DIFFICULTY_DECAY_RATE * daysAgo);  // Higher weight for recent attempts

                long minutes = ChronoUnit.MINUTES.between(attempt.getStartTime(), attempt.getEndTime());
                totalMinutes += minutes * weight;
                sumOfWeights += weight;
            }
        }

        double weighedAvgMinutes = totalMinutes / sumOfWeights;

        // Cap at maximum time and normalize to 0-100
        double capped = Math.min(weighedAvgMinutes, MAX_TIME_SPENT_MINUTES);
        return (int) ((capped * 100.0) / MAX_TIME_SPENT_MINUTES);
    }


    /**
     * Get score based on recency of last attempt and number of attempts
     * More recent = lower priority (already practiced recently)
     * Older = higher priority (needs review)
     */
    private int getRecencyScore(Problem myProblem, User myUser) {

        double HALF_OF_PERCENTAGE_SCORE = 50.0;

        List<UserProblemAttempt> attempts = myUser.getListAttempts().stream()
                .filter(p -> p.getProblem().getProblemId() == myProblem.getProblemId()).toList();

        if (attempts.isEmpty()) {
            return 100; // Never attempted = highest priority
        }

        LocalDateTime latestAttempt = null;
        long daysSince = 0;

        // More attempts indicate familiarity with question
        int attemptCount = attempts.size();

        // attempt factor calculation using both attempt count and time
        double attemptCountFactor = 0;

        for (UserProblemAttempt attempt : attempts) {
            if( attempt.getEndTime() != null && ( latestAttempt == null || attempt.getEndTime().isAfter(latestAttempt) ) ) {
                latestAttempt = attempt.getEndTime();
            }
            if (attempt.getStartTime() != null && attempt.getEndTime() != null) {
                long daysAgo = ChronoUnit.DAYS.between(attempt.getEndTime(), LocalDateTime.now(ZoneOffset.UTC));
                double weight = Math.exp(-DIFFICULTY_DECAY_RATE * daysAgo);  // Higher weight for recent attempts

                attemptCountFactor += (HALF_OF_PERCENTAGE_SCORE / attemptCount) * weight;
            }
        }

        // More days = higher score (capped at RECENCY_DAYS_MAX)
        if(latestAttempt != null) {
            // Calculate days since latest attempt
            daysSince = ChronoUnit.DAYS.between(latestAttempt, LocalDateTime.now(ZoneOffset.UTC));
            long cappedDays = Math.min(daysSince, RECENCY_DAYS_MAX);
            return (int) ( ((cappedDays * HALF_OF_PERCENTAGE_SCORE) / RECENCY_DAYS_MAX) + attemptCountFactor );
        } else {
            return 100;
        }
    }

    public int getRetentionScore(Problem myProblem, User myUser) {

        List<UserProblemAttempt> attempts = myUser.getListAttempts().stream()
                .filter(p -> p.getProblem().getProblemId() == myProblem.getProblemId()).toList();

        Collections.sort(attempts, Comparator.comparing(UserProblemAttempt::getEndTime));

        double memoryStrength = switch (myProblem.getDifficultyLevel().toLowerCase()) {
            case "easy" -> 5.0;
            case "medium" -> 3.0;
            case "hard" -> 1.0;
            default -> 3.0;
        };

        // In getRetentionScore method
        if (attempts.size() > 1) {
            // Give more weight to recent attempts
            for (int i = 1; i < attempts.size(); i++) {
                int prevRating = attempts.get(i-1).getDifficultyRating();
                int currentRating = attempts.get(i).getDifficultyRating();

                // Calculate weight based on position in attempt history
                double attemptWeight = (double)i / attempts.size(); // More recent attempts get higher weight

                if (currentRating < prevRating) {
                    // Decreasing difficulty = getting easier = success
                    memoryStrength *= 1.0 + (0.3 * attemptWeight);
                } else if (currentRating > prevRating) {
                    // Increasing difficulty = getting harder = struggle
                    memoryStrength *= 1.0 - (0.3 * attemptWeight);
                }
            }

            // Calculate days since last attempt
            UserProblemAttempt latestAttempt = attempts.getLast();
            long daysSinceLastAttempt = ChronoUnit.DAYS.between(latestAttempt.getEndTime(), LocalDateTime.now());

            // Calculate retention
            double retention = Math.exp(-daysSinceLastAttempt / memoryStrength);
            return (int) Math.min(1.0, Math.max(0.0, retention)) * 100;
        } else {
            return 50;
        }
    }


    /**
     * Calculate initial priority score when user first encounters a problem
     * This method uses problem metadata only (no attempt history)
     */
    public double calculateInitialPriorityScore(Problem problem, User user) {
        // Topic rank is the primary factor
        int topicRankScore = getTopicRankScore(problem, user);
        int difficultyScore = getDifficultyScore(problem, user);

        // Time spent and recency are maximized since user hasn't practiced this problem yet
        int timeSpentScore = 50;   // Default to middle value for time spent
        int recencyScore = 100;    // Maximum recency score (never attempted)
        int retentionScore = 0;

        // Calculate weighted sum
        double weightedScore = (TOPIC_RANK_WEIGHT * topicRankScore) +
                (DIFFICULTY_WEIGHT * difficultyScore) +
                (TIME_SPENT_WEIGHT * timeSpentScore) +
                (RECENCY_WEIGHT * recencyScore) +
                (RETENTION_WEIGHT * retentionScore);

        double uniqueFactor = generateUniqueFactor(problem);

        return Math.min(100, Math.max(0, weightedScore)) + (uniqueFactor / 100);
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
