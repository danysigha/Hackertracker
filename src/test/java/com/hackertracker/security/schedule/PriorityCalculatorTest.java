package com.hackertracker.security.schedule;

import com.hackertracker.security.dao.*;
import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.problem.TagProblem;
import com.hackertracker.security.problem.TopicProblem;
import com.hackertracker.security.problem.UserProblemPriorityService;
import com.hackertracker.security.tag.Tag;
import com.hackertracker.security.topic.Topic;
import com.hackertracker.security.user.Role;
import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserProblemAttempt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class PriorityCalculatorTest {

    @Autowired
    private ProblemDAO problemDao;

    @Autowired
    private UserDAO userDao;

    @Autowired
    private UserProblemAttemptDAO attemptDao;

    @Autowired
    private UserProblemPriorityService priorityService;

    @Autowired
    private TopicDAO topicDao;

    @Autowired
    private TagDAO tagDao;

    @Autowired
    private TopicProblemDAO topicProblemDao;

    @Autowired
    private TagProblemDAO tagProblemDao;

    private static final int MAX_TOPIC_RANK = 23;

    private static final int MAX_DIFFICULTY_RATING = 10;

    private static final double DIFFICULTY_DECAY_RATE = 0.1;

    private User user;
//    private Problem problem1;
//    private Problem problem2;
//    private Problem problem3;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setFirstName("test");
        user.setLastName("test");
        user.setUserName("testusername");
        user.setPassword("S!x64%KyobgNk8p");
        user.setEmail("test@gmail.com");
        user.setRole(Role.USER);
        userDao.saveUser(user);
        this.user = user;


//        Tag tag1 = new Tag();
//        tag1.setTagName("Tag 1");
//
//        Tag tag2 = new Tag();
//        tag2.setTagName("Tag 2");
//
//        Tag tag3 = new Tag();
//        tag3.setTagName("Tag 3");
//
//        tagDao.saveTag(tag1);
//        tagDao.saveTag(tag2);
//        tagDao.saveTag(tag3);
//
//        Problem problem1 = new Problem();
//        problem1.setQuestionTitle("Problem 1");
//        problem1.setDifficultyLevel("Easy");
//        problem1.setPageUrl("www.test.com");
//        problemDao.saveProblem(problem1);
//
//        TagProblem tagProblem1 = new TagProblem(problem1, tag1);
//        tagProblemDao.saveTagProblem(tagProblem1);
//        problem1.addTag(tag1, tagProblem1);
//
//        TagProblem tagProblem2 = new TagProblem(problem1, tag2);
//        tagProblemDao.saveTagProblem(tagProblem2);
//        problem1.addTag(tag2, tagProblem2);
//
//        this.problem1 = problem1;

        priorityService.initializeAllPrioritiesForNewUser(this.user);
    }

    @AfterEach
    void tearDown() {
        userDao.deleteUser(this.user.getUserId());
    }


    @Test
    void testGetTopicRankScoreMultipleCases() {
        PriorityCalculator priorityCalculator = new PriorityCalculator();

        // Create topics with different ranks
        Topic topic1 = new Topic();
        topic1.setTopicName("Topic 1");
        topic1.setTopicRank((byte) 1);  // Highest priority

        Topic topic2 = new Topic();
        topic2.setTopicName("Topic 2");
        topic2.setTopicRank((byte) 2);

        Topic topic3 = new Topic();
        topic3.setTopicName("Topic 3");
        topic3.setTopicRank((byte) 3);

        topicDao.saveTopicByUserId(topic1, user.getUserId());
        topicDao.saveTopicByUserId(topic2, user.getUserId());
        topicDao.saveTopicByUserId(topic3, user.getUserId());

        User testUser = userDao.getUserByUserName(user.getUserName());

        // Case 1: Problem with no topics
        Problem emptyProblem = new Problem();
        emptyProblem.setQuestionTitle("Empty Problem");
        emptyProblem.setDifficultyLevel("Medium");
        emptyProblem.setPageUrl("www.test.com/empty");
        problemDao.saveProblem(emptyProblem);

        int emptyResult = priorityCalculator.getTopicRankScore(emptyProblem, testUser);
        assertEquals(50, emptyResult, "Problem with no topics should return 50");

        // Case 2: Problem with multiple topics (should pick rank 1)
        Problem multiTopicProblem = new Problem();
        multiTopicProblem.setQuestionTitle("Multi Topic Problem");
        multiTopicProblem.setDifficultyLevel("Hard");
        multiTopicProblem.setPageUrl("www.test.com/multi");
        problemDao.saveProblem(multiTopicProblem);

// Add topic1 (rank 1)
        TopicProblem topicProblem1 = new TopicProblem(multiTopicProblem, topic1);
        topicProblemDao.saveTopicProblem(topicProblem1);
        multiTopicProblem.addTopic(topic1, topicProblem1);

// Add topic2 (rank 2)
        TopicProblem topicProblem2 = new TopicProblem(multiTopicProblem, topic2);
        topicProblemDao.saveTopicProblem(topicProblem2);
        multiTopicProblem.addTopic(topic2, topicProblem2);

// Add topic3 (rank 3)
        TopicProblem topicProblem3 = new TopicProblem(multiTopicProblem, topic3);
        topicProblemDao.saveTopicProblem(topicProblem3);
        multiTopicProblem.addTopic(topic3, topicProblem3);

        int multiResult = priorityCalculator.getTopicRankScore(multiTopicProblem, testUser);

        int expectedBestRank = 1; // Should pick rank 1 (highest priority)
        int expectedScore = (int) (100 - (expectedBestRank * 100.0 / MAX_TOPIC_RANK));

        assertEquals(expectedScore, multiResult, "Should pick the highest priority topic (rank 1)");
    }


    /**
     * STEPS for manually computing the difficulty score:
     *
     * 1. If there are no attempts:
     *    - Score is simply the question's difficulty level expressed out of 100
     *      (Easy = 80, Medium = 50, Hard = 20)
     *
     * 2. For questions with attempts:
     *    a) Find the most recent attempt's difficulty rating
     *    b) Calculate time-weighted average of all attempts:
     *       - Formula: Σ(e^(-0.1 * daysAgo) * difficultyRating) / Σ(e^(-0.1 * daysAgo))
     *       - This gives more weight to recent attempts and less to older ones
     *
     *    c) Compare latest rating to weighted average:
     *       - If latest < weighted avg (problem getting easier):
     *         * With ≤ 3 attempts: Reduce latest rating by 1 point
     *         * With > 3 attempts: Reduce latest rating by 1.5 points
     *
     *    d) Check variance in difficulty ratings:
     *       - If variance > 2.0 (inconsistent understanding):
     *         * Increase latest rating by 0.5 points
     *
     *    e) Apply objective/subjective weighting:
     *       - With < 5 attempts:
     *         * Objective weight = max(0, 1.0 - (attempts.size() * 0.2))
     *           (decreases as attempt count increases)
     *         * User perception weight = 1.0 - objective weight
     *         * Final score = (objectiveWeight * difficultyLevelScore) +
     *                        (userWeight * (latestRating * 100 / MAX_DIFFICULTY_RATING))
     *
     *       - With ≥ 5 attempts:
     *         * Trust user perception entirely
     *         * Final score = latestRating * 100 / MAX_DIFFICULTY_RATING
     */
    @ParameterizedTest
    @MethodSource("difficultyScenarios")
    void testDifficultyScoreCalculation(String description, Problem problem,
                                        List<UserProblemAttempt> attempts, int expectedScore, boolean shouldBoost) {
        // Setup test with given problem and attempts
        // ...
        User testUser = userDao.getUserById(this.user.getUserId()); // Retrieve mock user from DB

        problemDao.saveProblem(problem); // Save mock problem to DB

        for(UserProblemAttempt atp : attempts) {
            atp.setProblem(problem);
            atp.setUser(testUser);
            attemptDao.saveAttempt(atp); // Save mock attempts to DB
        }

        // Create a mock Random that returns a predictable value for nextDouble()
        Function<Integer, Random> mockRandomProvider = seed -> {
            Random mockRandom = mock(Random.class);
            // If shouldBoost is true, return a small value to trigger the boost
            // Otherwise return a value > boostChance to avoid boost
            when(mockRandom.nextDouble()).thenReturn(shouldBoost ? 0.05 : 0.95);
            return mockRandom;
        };

        PriorityCalculator priorityCalculator = new PriorityCalculator(mockRandomProvider);

        int actualScore = priorityCalculator.getDifficultyScore(problem, testUser);

        assertEquals(expectedScore, actualScore, description);
    }

     static Stream<Arguments> difficultyScenarios() {
        return Stream.of(
                Arguments.of("No attempts - Medium problem", createProblem("Medium"), Collections.emptyList(), 75, true),
                Arguments.of("No attempts - Easy problem", createProblem("Hard"), Collections.emptyList(), 20, false),
                Arguments.of("Two attempts - higher latest rating", createProblem("Medium"),
                        Arrays.asList(createAttempt( (byte) 5, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30)),
                                createAttempt((byte) 8, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2).plusMinutes(55))), 48, false),
                Arguments.of("Three attempts - higher second rating", createProblem("Hard"),
                        Arrays.asList(createAttempt( (byte) 5, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30)),
                                createAttempt((byte) 8, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2).plusMinutes(55)),
                                createAttempt((byte) 3, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(5).plusMinutes(20))), 35, false),
                Arguments.of("Three attempts - decreasing ratings", createProblem("Hard"),
                        Arrays.asList(createAttempt( (byte) 4, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30)),
                                createAttempt((byte) 7, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2).plusMinutes(45)),
                                createAttempt((byte) 10, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(5).plusMinutes(120))), 29, false),
                Arguments.of("Three attempts - increasing ratings", createProblem("Hard"),
                        Arrays.asList(createAttempt( (byte) 10, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(120)),
                                createAttempt((byte) 7, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2).plusMinutes(45)),
                                createAttempt((byte) 4, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(5).plusMinutes(30))), 68, false),
                Arguments.of("Four attempts - mixed ratings", createProblem("Medium"),
                        Arrays.asList(createAttempt( (byte) 7, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(45)),
                                createAttempt((byte) 3, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2).plusMinutes(20)),
                                createAttempt((byte) 5, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(5).plusMinutes(30)),
                                createAttempt((byte) 8, LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(7).plusMinutes(30))), 70, false),
                Arguments.of("Five attempts - mixed ratings", createProblem("Hard"),
                        Arrays.asList(createAttempt( (byte) 8, LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(50)),
                                createAttempt((byte) 6, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2).plusMinutes(40)),
                                createAttempt((byte) 3, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(5).plusMinutes(20)),
                                createAttempt((byte) 5, LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(7).plusMinutes(30)),
                                createAttempt((byte) 2, LocalDateTime.now().minusDays(8), LocalDateTime.now().minusDays(8).plusMinutes(20))), 85, false)
                // Add more test scenarios
        );
    }

     static Problem createProblem(String difficultyLevel) {
        Problem newProblem = new Problem();
        newProblem.setQuestionTitle("A new " + difficultyLevel + " problem");
        newProblem.setDifficultyLevel(difficultyLevel);
        newProblem.setPageUrl("www.test.com/"+difficultyLevel);
        return newProblem;
    }

    static UserProblemAttempt createAttempt(byte difficultyScore, LocalDateTime startTime, LocalDateTime endTime) {
        UserProblemAttempt theAttempt = new UserProblemAttempt();
        theAttempt.setDifficultyRating(difficultyScore);
        theAttempt.setStartTime(startTime);
        theAttempt.setEndTime(endTime);
        theAttempt.setNotes("<p>Some test notes.</p>");
        return theAttempt;
    }

}