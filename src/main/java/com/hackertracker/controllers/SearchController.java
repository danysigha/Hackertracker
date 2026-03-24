package com.hackertracker.controllers;

import com.hackertracker.dao.TagDAO;
import com.hackertracker.dao.TopicDAO;
import com.hackertracker.dao.UserDAO;
import com.hackertracker.dto.ProblemWithAttemptDTO;
import com.hackertracker.dto.TagDTO;
import com.hackertracker.dto.TopicDTO;
import com.hackertracker.problem.ProblemSearchService;
import com.hackertracker.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final ProblemSearchService problemSearchService;
    private final UserDAO userDao;
    private final TagDAO tagDao;
    private final TopicDAO topicDao;

    public SearchController(ProblemSearchService problemSearchService, UserDAO userDao, TagDAO tagDao, TopicDAO topicDao) {
        this.problemSearchService = problemSearchService;
        this.userDao = userDao;
        this.tagDao = tagDao;
        this.topicDao = topicDao;
    }

    /**
     * Get search results
     */
    @GetMapping("/challenges")
    public List<ProblemWithAttemptDTO> searchChallenges(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "topics", required = false) List<String> topics ,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "difficultyLevels", required = false) List<String> difficultyLevels,
            @RequestParam(value = "status", required = false) List<String> status,
            @AuthenticationPrincipal User user
    ) {

        User myUser = userDao.getUserByUserName(user.getUserName());

        Boolean completed = null;

        if(status != null) {
            if(status.size() == 1 && status.get(0).equals("Completed")) {
                completed = true;
            } else if( status.size() == 1 && status.get(0).equals("Not Completed")) {
                completed = false;
            }
        }

        return problemSearchService.searchProblems(title, tags, topics, difficultyLevels, completed, myUser.getUserId(), 150);
    }

    /**
     * Get the topic options
     */
    @GetMapping("/topics")
    public List<TopicDTO> getTopics() {
        return topicDao.getAllTopicsDtos();
    }

    /**
     * Get the tag options
     */
    @GetMapping("/tags")
    public List<TagDTO> getTags() {
        return tagDao.getAllTagDtos();
    }

}
