package com.hackertracker.security.controllers;

import com.hackertracker.security.dao.*;
import com.hackertracker.security.dto.ProblemWithAttemptDTO;
import com.hackertracker.security.dto.TopicDTO;
import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.problem.UserProblemPriorityService;
import com.hackertracker.security.topic.Topic;
import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserProblemAttempt;
import com.hackertracker.security.user.UserProblemCompletion;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


/**
 * Controller for problem priority-related endpoints
 */
@RestController
@RequestMapping("/api/challenges")
public class PriorityController {

    private final UserProblemPriorityService priorityService;
    private final UserDAO userDao;
    private final ProblemDAO problemDao;
    private final UserProblemCompletionDAO UserProblemCompletionDao;
    private final UserProblemAttemptDAO userProblemAttemptDao;
    private final TopicDAO topicDao;
    private final UserProblemPriorityService userProblemPriorityService;

    public PriorityController(UserProblemPriorityService priorityService,
                              UserDAO userDao, ProblemDAO problemDAO,
                              UserProblemCompletionDAO UserProblemCompletionDao,
                              UserProblemAttemptDAO userProblemAttemptDao,
                              TopicDAO topicDao, UserProblemPriorityService userProblemPriorityService){
        this.priorityService = priorityService;
        this.userDao = userDao;
        this.problemDao = problemDAO;
        this.UserProblemCompletionDao = UserProblemCompletionDao;
        this.userProblemAttemptDao = userProblemAttemptDao;
        this.topicDao = topicDao;
        this.userProblemPriorityService = userProblemPriorityService;
    }

    /**
     * Get the current user's prioritized problems
     */
    @GetMapping("/next")
    public ProblemWithAttemptDTO getPrioritizedProblemsOrById(
            @RequestParam(value = "questionId", required = false) String questionId,
            @RequestParam(value = "fetchById", required = false) boolean fetchById,
            @AuthenticationPrincipal User user) {

        User myUser = userDao.getUserByUserName(user.getUserName());

        try {
            Problem problem;
            UserProblemAttempt attempt;

            if(questionId == null || !fetchById) {
                // Get a problem ID first
                Problem basicProblem = priorityService.getNextTopPriorityProblemForUser(myUser);
                // Then fetch it with all collections
                problem = problemDao.getProblemByIdWithCollections(basicProblem.getProblemId());
            } else {
                problem = problemDao.getProblemByIdWithCollections(Integer.parseInt(questionId));
            }

            attempt = userProblemAttemptDao.getLatestAttempt(problem, myUser);

            return new ProblemWithAttemptDTO(problem, attempt);
        } catch (Exception e) {
            e.printStackTrace();
            return new ProblemWithAttemptDTO(null, null);
        }
    }

    /**
     * Manually trigger priority recalculation for the current user
     */
    @PostMapping("/recalculate")
    public Problem recalculatePriorities(
            @RequestParam(value = "questionId") String questionId,
            @RequestParam(value = "difficultyRating", required = false) String difficultyRating,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "notes", required = false) String notes,
            @AuthenticationPrincipal User user) {

        System.out.println("Receiving the following from loadview.js\n\n");
        System.out.println(questionId + " " + difficultyRating + " " + startTime + " " + endTime + " " + notes);

        Problem problem = problemDao.getProblemById(Integer.parseInt(questionId));

        User myUser = userDao.getUserByUserName(user.getUserName());

        Byte difficultyRatingByte = Byte.parseByte(difficultyRating);

        try {
            // Parse the time string to get a Date object
            UserProblemAttempt userProblemAttempt;

            if (!startTime.isEmpty() && !endTime.isEmpty()) {

                LocalDateTime startTimeDate = LocalDateTime.ofInstant(Instant.parse(startTime), ZoneOffset.UTC);
                LocalDateTime endTimeDate = LocalDateTime.ofInstant(Instant.parse(endTime), ZoneOffset.UTC);

                String cleanHtml = Jsoup.clean(notes, Safelist.relaxed());

                userProblemAttempt = new UserProblemAttempt(problem, myUser, difficultyRatingByte,
                        startTimeDate, endTimeDate, cleanHtml);
            } else {
                userProblemAttempt = new UserProblemAttempt();
                userProblemAttempt.setProblem(problem);
                userProblemAttempt.setUser(myUser);
                userProblemAttempt.setDifficultyRating(difficultyRatingByte);
                userProblemAttempt.setEndTime(LocalDateTime.now(ZoneOffset.UTC));

                // In your controller
                String cleanHtml = Jsoup.clean(notes, Safelist.relaxed());
                userProblemAttempt.setNotes(cleanHtml);

                // userProblemAttempt.setNotes(notes);
            }

            UserProblemCompletion completion = new UserProblemCompletion();
            completion.setCompletionDate(LocalDateTime.now(ZoneOffset.UTC));
            completion.setUser(user);
            completion.setProblem(problem);
            System.out.println("Completion supposedly getting saved.\n\n");
            System.out.println(completion);
            UserProblemCompletionDao.save(completion);

            System.out.println("\n\nAttempt supposedly getting saved.\n\n");
            System.out.println(userProblemAttempt);
            userProblemAttemptDao.saveAttempt(userProblemAttempt);

            priorityService.updatePriorityAfterAttempt(userProblemAttempt);

        } catch (Exception e) {
            e.printStackTrace();
        }

        priorityService.recalculateSinglePriority(problem, myUser);

        return priorityService.getNextTopPriorityProblemForUser(myUser);
    }


    /**
     * Get the programming topics
     */
    @GetMapping("/topics")
    public List<TopicDTO> getAllTopics() {
        List<TopicDTO> topics = new ArrayList<>();

        for(Topic topic : topicDao.getAllTopics()) {
            topics.add(TopicDTO.fromEntity(topic));
        }
        return topics;
    }

    @PostMapping("/update-topic-priority")
    public void updateTopicPriorities(@RequestBody List<TopicDTO> topics, @AuthenticationPrincipal User user) {
        User myUser = userDao.getUserByUserName(user.getUserName());

        for(TopicDTO topic : topics) {
            Topic myTopic = topicDao.getTopicById(topic.getTopicId());
            myTopic.setTopicRank(topic.getTopicRank());
            topicDao.updateTopic(myTopic);
        }
        userProblemPriorityService.recalculateAllPrioritiesByUser(myUser);
    }
}