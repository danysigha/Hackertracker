package com.hackertracker.security.dto;

import com.hackertracker.security.user.UserProblemAttempt;

import java.util.Date;

public class UserProblemAttemptDTO {
    private int attemptId;
    private byte difficultyRating;
    private String notes;
    private Date startTime;
    private Date endTime;

    // Default constructor
    public UserProblemAttemptDTO() {}

    // Static factory method
    public static UserProblemAttemptDTO fromEntity(UserProblemAttempt attempt) {
        if (attempt == null) return null;

        UserProblemAttemptDTO dto = new UserProblemAttemptDTO();
        dto.setAttemptId(attempt.getAttemptId());
        dto.setDifficultyRating(attempt.getDifficultyRating());
        dto.setNotes(attempt.getNotes());
        dto.setStartTime(attempt.getStartTime());
        dto.setEndTime(attempt.getEndTime());

        return dto;
    }

    // Getters and setters
    public int getAttemptId() { return attemptId; }
    public void setAttemptId(int attemptId) { this.attemptId = attemptId; }

    public byte getDifficultyRating() { return difficultyRating; }
    public void setDifficultyRating(byte difficultyRating) { this.difficultyRating = difficultyRating; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
}