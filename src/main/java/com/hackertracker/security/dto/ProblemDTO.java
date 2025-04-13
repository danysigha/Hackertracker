package com.hackertracker.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDTO {
    private int problemId;
    private String publicProblemId;
    private String questionTitle;
    private String difficultyLevel;
    private String pageUrl;
    private List<TopicDTO> topics;
    private List<UserProblemAttemptDTO> attempts;

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