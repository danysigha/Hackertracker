package com.hackertracker.security.dto;

import com.hackertracker.security.problem.Problem;
import java.util.ArrayList;
import java.util.List;

public class ProblemDTO {
    private int problemId;
    private String publicProblemId;
    private String questionTitle;
    private String difficultyLevel;
    private String pageUrl;
    private List<String> problemTags = new ArrayList<>();
    private List<String> problemTopics = new ArrayList<>();

    // Default constructor
    public ProblemDTO() {}

    // Static factory method to create from entity
    public static ProblemDTO fromEntity(Problem problem) {
        if (problem == null) return null;

        ProblemDTO dto = new ProblemDTO();
        dto.setProblemId(problem.getProblemId());
        dto.setPublicProblemId(problem.getPublicProblemId());
        dto.setQuestionTitle(problem.getQuestionTitle());
        dto.setDifficultyLevel(problem.getDifficultyLevel());
        dto.setPageUrl(problem.getPageUrl());

        // Extract tag names if collection is initialized
        if (problem.getProblemTags() != null) {
            problem.getProblemTags().forEach(tagProblem -> {
                if (tagProblem.getTag() != null) {
                    dto.getProblemTags().add(tagProblem.getTag().getTagName());
                }
            });
        }

        // Extract topic names if collection is initialized
        if (problem.getProblemTopics() != null) {
            problem.getProblemTopics().forEach(topicProblem -> {
                if (topicProblem.getTopic() != null) {
                    dto.getProblemTopics().add(topicProblem.getTopic().getTopicName());
                }
            });
        }

        return dto;
    }

    // Getters and setters
    public int getProblemId() { return problemId; }
    public void setProblemId(int problemId) { this.problemId = problemId; }

    public String getPublicProblemId() { return publicProblemId; }
    public void setPublicProblemId(String publicProblemId) { this.publicProblemId = publicProblemId; }

    public String getQuestionTitle() { return questionTitle; }
    public void setQuestionTitle(String questionTitle) { this.questionTitle = questionTitle; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }

    public List<String> getProblemTags() { return problemTags; }
    public void setProblemTags(List<String> problemTags) { this.problemTags = problemTags; }

    public List<String> getProblemTopics() { return problemTopics; }
    public void setProblemTopics(List<String> problemTopics) { this.problemTopics = problemTopics; }
}