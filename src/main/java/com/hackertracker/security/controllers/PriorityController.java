package com.hackertracker.security.controllers;

import com.hackertracker.security.dto.ProblemWithAttemptDTO;
import com.hackertracker.security.dao.ProblemDAO;
import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.dao.UserProblemAttemptDAO;
import com.hackertracker.security.dao.UserProblemPriorityDAO;
import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.problem.UserProblemPriorityService;
import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserProblemAttempt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.time.Instant;
import java.util.Date;


/**
 * Controller for problem priority-related endpoints
 */
@RestController
@RequestMapping("/api/challenges")
public class PriorityController {

    private final UserProblemPriorityService priorityService;
    private final UserDAO userDao;
    private final ProblemDAO problemDao;
    private final UserProblemPriorityDAO userProblemPriorityDao;
    private final UserProblemAttemptDAO userProblemAttemptDao;

    public PriorityController(UserProblemPriorityService priorityService,
                              UserDAO userDao, ProblemDAO problemDAO,
                              UserProblemPriorityDAO userProblemPriorityDao,
                              UserProblemAttemptDAO userProblemAttemptDao){
        this.priorityService = priorityService;
        this.userDao = userDao;
        this.problemDao = problemDAO;
        this.userProblemPriorityDao = userProblemPriorityDao;
        this.userProblemAttemptDao = userProblemAttemptDao;
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

        Problem problem = problemDao.getProblemById(Integer.parseInt(questionId));

        User myUser = userDao.getUserByUserName(user.getUserName());

        Byte difficultyRatingByte = Byte.parseByte(difficultyRating);

        try {
            // Parse the time string to get a Date object
            UserProblemAttempt userProblemAttempt;

            if (startTime != null && endTime != null) {
                Date startTimeDate = Date.from(Instant.parse(startTime));
                Date endTimeDate = Date.from(Instant.parse(endTime));

                String cleanHtml = Jsoup.clean(notes, Safelist.relaxed());

                userProblemAttempt = new UserProblemAttempt(problem, myUser, difficultyRatingByte,
                        startTimeDate, endTimeDate, cleanHtml);
            } else {
                userProblemAttempt = new UserProblemAttempt();
                userProblemAttempt.setProblem(problem);
                userProblemAttempt.setUser(myUser);
                userProblemAttempt.setDifficultyRating(difficultyRatingByte);

                // In your controller
                String cleanHtml = Jsoup.clean(notes, Safelist.relaxed());
                userProblemAttempt.setNotes(cleanHtml);

                // userProblemAttempt.setNotes(notes);
            }

            userProblemAttemptDao.saveAttempt(userProblemAttempt);

            priorityService.updatePriorityAfterAttempt(userProblemAttempt);

        } catch (Exception e) {
            e.printStackTrace();
        }

        priorityService.recalculateSinglePriority(problem, user);

        return priorityService.getNextTopPriorityProblemForUser(user);
    }
}