package com.hackertracker.security.user;

import com.hackertracker.security.dao.ProblemDAO;
import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.dto.ProblemDTO;
import com.hackertracker.security.dto.UserProblemPriorityDTO;
import com.hackertracker.security.dto.UserProblemService;
import com.hackertracker.security.problem.UserProblemPriorityService;
import com.hackertracker.security.dto.TopicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for problem priority-related endpoints
 */
@RestController
@RequestMapping("/api/priorities")
public class PriorityController {

    private final UserProblemPriorityService priorityService;
    private final UserProblemService userProblemService;

    public PriorityController(UserProblemPriorityService priorityService, UserProblemService userProblemService){
        this.priorityService = priorityService;
        this.userProblemService = userProblemService;
    }

    /**
     * Get the current user's prioritized problems
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getPrioritizedProblems(
            @AuthenticationPrincipal User user) {

        List<ProblemDTO> prioritizedProblems = priorityService.getPrioritizedProblemsForUser(user);

        // Map problems to a simplified response format
        List<Map<String, Object>> result = prioritizedProblems.stream()
                .map(problemDto -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("problemId", problemDto.getProblemId());
                    map.put("publicProblemId", problemDto.getPublicProblemId());
                    map.put("title", problemDto.getQuestionTitle());
                    map.put("difficulty", problemDto.getDifficultyLevel());
                    map.put("url", problemDto.getPageUrl());

                    // Include topics
                    List<String> topics = problemDto.getTopics().stream()
                            .map(TopicDTO::getTopicName)
                            .collect(Collectors.toList());
                    map.put("topics", topics);

                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Manually trigger priority recalculation for the current user
     */
    @PostMapping("/recalculate")
    public ResponseEntity<Map<String, String>> recalculatePriorities(
            @AuthenticationPrincipal User user) {

        List<UserProblemPriorityDTO> priorities = userProblemService.getUserPriorities(user);

        for (UserProblemPriorityDTO priority : priorities) {
            ProblemDTO problemDto = priority.getProblemDto();
            double newScore = priorityService.recalculateSinglePriority(problemDto, user);
            priority.setPriorityScore(newScore);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Priorities recalculated successfully");
        return ResponseEntity.ok(response);
    }
}