package com.hackertracker.security.dto;

import java.util.List;
import java.util.Objects;

public class ProblemDTO {
    private int problemId;
    private String publicProblemId;
    private String questionTitle;
    private String difficultyLevel;
    private String pageUrl;
    private List<TopicDTO> topics;
    private List<UserProblemAttemptDTO> attempts;

    public ProblemDTO() {
    }

    public ProblemDTO(int problemId, String publicProblemId, String questionTitle, String pageUrl, String difficultyLevel, List<TopicDTO> topics, List<UserProblemAttemptDTO> attempts) {
        this.problemId = problemId;
        this.publicProblemId = publicProblemId;
        this.questionTitle = questionTitle;
        this.pageUrl = pageUrl;
        this.difficultyLevel = difficultyLevel;
        this.topics = topics;
        this.attempts = attempts;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public String getPublicProblemId() {
        return publicProblemId;
    }

    public void setPublicProblemId(String publicProblemId) {
        this.publicProblemId = publicProblemId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public List<TopicDTO> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDTO> topics) {
        this.topics = topics;
    }

    public List<UserProblemAttemptDTO> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<UserProblemAttemptDTO> attempts) {
        this.attempts = attempts;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "problemId=" + problemId + ", " +
                "publicProblemId=" + publicProblemId +
                ", questionTitle=" + questionTitle + "\n" +
                ", difficultyLevel=" + difficultyLevel + "\n" +
                ", pageUrl=" + pageUrl + "\n" +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProblemDTO problem = (ProblemDTO) o;
        return problemId == problem.problemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemId);
    }
}