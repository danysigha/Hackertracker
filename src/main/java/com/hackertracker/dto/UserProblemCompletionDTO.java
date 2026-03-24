package com.hackertracker.dto;

import com.hackertracker.user.UserProblemCompletion;

import java.time.LocalDateTime;

public class UserProblemCompletionDTO {
    private int completionId;
    private LocalDateTime completionDate;
    private int userId;
    private int problemId;

    public UserProblemCompletionDTO() {
    }

    // Static factory method to create from entity
    public static UserProblemCompletionDTO fromEntity(UserProblemCompletion upc) {
        if (upc == null) return null;

        UserProblemCompletionDTO dto = new UserProblemCompletionDTO();
        dto.setUserId(upc.getUser().getUserId());
        dto.setProblemId(upc.getProblem().getProblemId());
        dto.setCompletionId(upc.getCompletionId());
        dto.setCompletionDate(upc.getCompletionDate());

        return dto;
    }

    public int getCompletionId() {
        return completionId;
    }

    public void setCompletionId(int completionId) {
        this.completionId = completionId;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }
}
