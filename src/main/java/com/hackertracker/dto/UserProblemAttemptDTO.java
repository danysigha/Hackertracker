package com.hackertracker.dto;

import com.hackertracker.user.UserProblemAttempt;

import java.time.LocalDateTime;

public class UserProblemAttemptDTO {
    private int attemptId;
    private byte difficultyRating;
    private String notes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

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

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}