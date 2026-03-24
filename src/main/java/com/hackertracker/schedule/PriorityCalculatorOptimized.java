package com.hackertracker.schedule;

import com.hackertracker.problem.Problem;
import com.hackertracker.topic.Topic;
import com.hackertracker.user.UserProblemAttempt;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class PriorityCalculatorOptimized {

    private static final double DIFFICULTY_DECAY_RATE = 0.1; // Time decay parameter

    private static final double TOPIC_RANK_WEIGHT = 0.3;     // 20% weight for topic rank
    private static final double DIFFICULTY_WEIGHT = 0.2;     // 20% weight for difficulty
    private static final double RETENTION_WEIGHT = 0.3;      // 40% weight for retention
    private static final double TIME_SPENT_WEIGHT = 0.1;    // 10% weight for time spent
    private static final double RECENCY_WEIGHT = 0.1;       // 10% weight for recency

    // Constants for normalization
    private static final int MAX_DIFFICULTY_RATING = 10;      // Assuming difficulty rating is 1-10
    private static final int MAX_TOPIC_RANK = 23;           // Assuming topic ranks are 1-23
    private static final long MAX_TIME_SPENT_MINUTES = 120;  // Cap time spent at 2 hours for scoring
    private static final long RECENCY_DAYS_MAX = 14;         // Consider attempts within last 14 days

    PriorityCalculatorOptimized(){}


    // Add this method to PriorityCalculatorOptimized if it doesn't exist yet
    public double calculateInitialPriorityScoreOptimized(
            Problem problem,
            List<UserProblemAttempt> problemAttempts, // Empty for new users
            List<Byte> userTopicRanks) {

        // Topic rank is the primary factor
        int topicRankScore = getTopicRankScoreOptimized(problem, userTopicRanks);
        int difficultyScore = convertDifficultyLevelToScore(problem.getDifficultyLevel());

        // Time spent and recency are maximized since user hasn't practiced this problem yet
        int timeSpentScore = 50;   // Default to middle value for time spent
        int recencyScore = 100;    // Maximum recency score (never attempted)
        int retentionScore = 0;    // No retention for new problems

        // Calculate weighted sum
        double weightedScore = (TOPIC_RANK_WEIGHT * topicRankScore) +
                (DIFFICULTY_WEIGHT * difficultyScore) +
                (TIME_SPENT_WEIGHT * timeSpentScore) +
                (RECENCY_WEIGHT * recencyScore) +
                (RETENTION_WEIGHT * retentionScore);

        double uniqueFactor = generateUniqueFactor(problem);

        return Math.min(100, Math.max(0, weightedScore)) + (uniqueFactor / 100);
    }

    // Main optimized calculation method
    public double calculatePriorityScoreOptimized(
            Problem problem,
            List<UserProblemAttempt> problemAttempts,
            List<Byte> userTopicRanks) {

        // Calculate individual components
        int topicRankScore = getTopicRankScoreOptimized(problem, userTopicRanks);
        int difficultyScore = getDifficultyScoreOptimized(problem, problemAttempts);
        int timeSpentScore = getTimeSpentScoreOptimized(problemAttempts);
        int recencyScore = getRecencyScoreOptimized(problemAttempts);
        int retentionScore = getRetentionScoreOptimized(problem, problemAttempts);

        // Calculate weighted sum (same formula as original)
        double weightedScore = (TOPIC_RANK_WEIGHT * topicRankScore) +
                (DIFFICULTY_WEIGHT * difficultyScore) +
                (TIME_SPENT_WEIGHT * timeSpentScore) +
                (RECENCY_WEIGHT * recencyScore) +
                (RETENTION_WEIGHT * (1 - retentionScore / 100.0)); // Convert to 0-1 range

        // Add small unique factor for consistent tie-breaking
        double uniqueFactor = generateUniqueFactor(problem);

        // Clamp to 0-100 range
        return Math.min(100, Math.max(0, weightedScore)) + (uniqueFactor / 100);
    }

    // Optimized topic rank score calculation
    private int getTopicRankScoreOptimized(Problem problem, List<Byte> userTopicRanks) {
        int highestRank = -1;

        List<Topic> topics = problem.getListTopics(); // This is already loaded
        if (topics.isEmpty()) {
            return 50; // Default middle value if no topics
        }

        for (Topic topic : topics) {
            int topicId = topic.getTopicId();
            // Check array bounds to be safe
            if (topicId > 0 && topicId <= userTopicRanks.size()) {
                int currentRank = userTopicRanks.get(topicId - 1).intValue();
                if (highestRank == -1 || currentRank < highestRank) {
                    highestRank = currentRank;
                }
            }
        }

        if (highestRank > -1) {
            // Inverted normalization: rank 1 gets 100, MAX_TOPIC_RANK gets 0
            return (int) (100 - (highestRank * 100.0 / MAX_TOPIC_RANK));
        } else {
            return 50; // Default middle value
        }
    }

    // Optimized difficulty score calculation
    private int getDifficultyScoreOptimized(Problem problem, List<UserProblemAttempt> problemAttempts) {
        if (problemAttempts.isEmpty()) {
            // If no attempts, use problem's difficulty level
            return convertDifficultyLevelToScore(problem.getDifficultyLevel());
        }

        // Sort attempts by time (most recent first)
        List<UserProblemAttempt> sortedAttempts = new ArrayList<>(problemAttempts);
        sortedAttempts.sort((a1, a2) -> a2.getEndTime().compareTo(a1.getEndTime()));

        // Get latest rating
        double latestRating = sortedAttempts.get(0).getDifficultyRating();

        // Calculate time-weighted average
        double sumOfWeightedRatings = 0.0;
        double sumOfWeights = 0.0;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        for (UserProblemAttempt attempt : sortedAttempts) {
            if (attempt.getEndTime() != null) {
                long daysAgo = ChronoUnit.DAYS.between(attempt.getEndTime(), now);
                double weight = Math.exp(-DIFFICULTY_DECAY_RATE * daysAgo);

                sumOfWeightedRatings += attempt.getDifficultyRating() * weight;
                sumOfWeights += weight;
            }
        }

        double weightedAvgRating = sumOfWeights > 0 ?
                sumOfWeightedRatings / sumOfWeights : 5.0;

        // If latest rating is lower than average, reduce score
        if (latestRating < weightedAvgRating) {
            if (sortedAttempts.size() > 3) {
                latestRating = Math.max(1, latestRating - 1.5);
            } else {
                latestRating = Math.max(1, latestRating - 1);
            }
        }

        // Check for variance
        double variance = calculateVarianceOptimized(sortedAttempts);
        if (variance > 2.0) {
            // If ratings vary widely, increase difficulty
            latestRating = Math.min(10, latestRating + 0.5);
        }

        // Integration with problem difficulty for users with few attempts
        if (sortedAttempts.size() < 5) {
            int problemDifficulty = convertDifficultyLevelToScore(problem.getDifficultyLevel());
            double userPerception = ((latestRating * 100.0) / MAX_DIFFICULTY_RATING);

            double objectiveWeight = Math.max(0, 1.0 - (sortedAttempts.size() * 0.2));
            double userWeight = 1.0 - objectiveWeight;

            return (int) ((problemDifficulty * objectiveWeight) + (userPerception * userWeight));
        }

        // For users with more attempts, trust their perception
        return (int) ((latestRating * 100.0) / MAX_DIFFICULTY_RATING);
    }

    // Optimized variance calculation
    private double calculateVarianceOptimized(List<UserProblemAttempt> attempts) {
        if (attempts.size() <= 1) {
            return 0.0;
        }

        // Calculate mean
        double sum = 0;
        for (UserProblemAttempt attempt : attempts) {
            sum += attempt.getDifficultyRating();
        }
        double mean = sum / attempts.size();

        // Calculate sum of squared differences
        double sumSquaredDiff = 0;
        for (UserProblemAttempt attempt : attempts) {
            double diff = attempt.getDifficultyRating() - mean;
            sumSquaredDiff += diff * diff;
        }

        // Return variance (using n-1 for sample variance)
        return sumSquaredDiff / (attempts.size() - 1);
    }

    // Optimized time spent score calculation
    private int getTimeSpentScoreOptimized(List<UserProblemAttempt> problemAttempts) {
        if (problemAttempts.isEmpty()) {
            return 50; // Default middle value
        }

        // Calculate weighted average time spent
        double totalMinutes = 0;
        double sumOfWeights = 0;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        for (UserProblemAttempt attempt : problemAttempts) {
            if (attempt.getStartTime() != null && attempt.getEndTime() != null) {
                long daysAgo = ChronoUnit.DAYS.between(attempt.getEndTime(), now);
                double weight = Math.exp(-DIFFICULTY_DECAY_RATE * daysAgo);

                long minutes = ChronoUnit.MINUTES.between(attempt.getStartTime(), attempt.getEndTime());
                totalMinutes += minutes * weight;
                sumOfWeights += weight;
            }
        }

        // If no valid attempts with time data
        if (sumOfWeights == 0) {
            return 50;
        }

        double weightedAvgMinutes = totalMinutes / sumOfWeights;

        // Cap at maximum time and normalize to 0-100
        double capped = Math.min(weightedAvgMinutes, MAX_TIME_SPENT_MINUTES);
        return (int) ((capped * 100.0) / MAX_TIME_SPENT_MINUTES);
    }

    // Optimized recency score calculation
    private int getRecencyScoreOptimized(List<UserProblemAttempt> problemAttempts) {
        double HALF_OF_PERCENTAGE_SCORE = 50.0;

        if (problemAttempts.isEmpty()) {
            return 100; // Never attempted = highest priority
        }

        LocalDateTime latestAttempt = null;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        int attemptCount = problemAttempts.size();
        double attemptCountFactor = 0;

        for (UserProblemAttempt attempt : problemAttempts) {
            if (attempt.getEndTime() != null) {
                // Find latest attempt
                if (latestAttempt == null || attempt.getEndTime().isAfter(latestAttempt)) {
                    latestAttempt = attempt.getEndTime();
                }

                // Calculate time-weighted attempt factor
                if (attempt.getStartTime() != null) {
                    long daysAgo = ChronoUnit.DAYS.between(attempt.getEndTime(), now);
                    double weight = Math.exp(-DIFFICULTY_DECAY_RATE * daysAgo);
                    attemptCountFactor += (HALF_OF_PERCENTAGE_SCORE / attemptCount) * weight;
                }
            }
        }

        // If we found a latest attempt
        if (latestAttempt != null) {
            long daysSince = ChronoUnit.DAYS.between(latestAttempt, now);
            long cappedDays = Math.min(daysSince, RECENCY_DAYS_MAX);
            return (int) (((cappedDays * HALF_OF_PERCENTAGE_SCORE) / RECENCY_DAYS_MAX) + attemptCountFactor);
        } else {
            return 100;
        }
    }

    // Optimized retention score calculation
    private int getRetentionScoreOptimized(Problem problem, List<UserProblemAttempt> problemAttempts) {
        if (problemAttempts.isEmpty() || problemAttempts.size() == 1) {
            return 50; // Default value for no or single attempt
        }

        // Sort attempts by time (oldest first)
        List<UserProblemAttempt> sortedAttempts = new ArrayList<>(problemAttempts);
        sortedAttempts.sort(Comparator.comparing(UserProblemAttempt::getEndTime));

        // Initial memory strength based on difficulty
        double memoryStrength = switch (problem.getDifficultyLevel().toLowerCase()) {
            case "easy" -> 5.0;
            case "medium" -> 3.0;
            case "hard" -> 1.0;
            default -> 3.0;
        };

        // Adjust based on difficulty progression
        for (int i = 1; i < sortedAttempts.size(); i++) {
            int prevRating = sortedAttempts.get(i-1).getDifficultyRating();
            int currentRating = sortedAttempts.get(i).getDifficultyRating();

            // Weight based on position
            double attemptWeight = (double)i / sortedAttempts.size();

            if (currentRating < prevRating) {
                // Getting easier = success
                memoryStrength *= 1.0 + (0.3 * attemptWeight);
            } else if (currentRating > prevRating) {
                // Getting harder = struggle
                memoryStrength *= 1.0 - (0.3 * attemptWeight);
            }
        }

        // Calculate days since last attempt
        UserProblemAttempt latestAttempt = sortedAttempts.get(sortedAttempts.size() - 1);
        long daysSinceLastAttempt = ChronoUnit.DAYS.between(
                latestAttempt.getEndTime(),
                LocalDateTime.now(ZoneOffset.UTC));

        // Calculate retention
        double retention = Math.exp(-daysSinceLastAttempt / memoryStrength);
        return (int) (Math.min(1.0, Math.max(0.0, retention)) * 100);
    }

    // Helper method to convert difficulty level string to score
    private int convertDifficultyLevelToScore(String difficultyLevel) {
        return switch (difficultyLevel.toLowerCase()) {
            case "easy" -> 65;
            case "medium" -> 55;
            case "hard" -> 45;
            default -> 55;
        };
    }

    // Generate a small unique factor for consistent tie-breaking
    private double generateUniqueFactor(Problem problem) {
        int hashValue = problem.hashCode();
        if (hashValue < 0) {
            hashValue = -hashValue; // Ensure positive value
        }
        return (hashValue % 100) / 100.0;
    }
}
