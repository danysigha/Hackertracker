package com.hackertracker.security.dto;

import java.util.Date;
import java.util.Objects;


public class UserProblemAttemptDTO {
    public int getAttemptId() {
        return attemptId;
    }

    public UserProblemAttemptDTO(int attemptId, byte difficultyRating, Date startTime, Date endTime, ProblemDTO problemDto) {
        this.attemptId = attemptId;
        this.difficultyRating = difficultyRating;
        this.startTime = startTime;
        this.endTime = endTime;
        this.problemDto = problemDto;
    }

    public UserProblemAttemptDTO() {
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public byte getDifficultyRating() {
        return difficultyRating;
    }

    public void setDifficultyRating(byte difficultyRating) {
        this.difficultyRating = difficultyRating;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public ProblemDTO getProblemDto() {
        return problemDto;
    }

    public void setProblemDto(ProblemDTO problemDto) {
        this.problemDto = problemDto;
    }

    private int attemptId;
    private byte difficultyRating;
    private Date startTime;
    private Date endTime;
    private ProblemDTO problemDto;

    @Override
    public String toString() {
        return "UserProblemAttempt{" +
                "attemptId=" + attemptId +
                ", problemId=" + (problemDto != null ? problemDto.getProblemId() : null) +
//                ", userId=" + (user != null ? user.getId() : null) +
                ", difficultyRating=" + (difficultyRating != 0 ? difficultyRating : null) +
                ", startTime=" + (startTime != null ? startTime : null) +
                ", endTime=" + (endTime != null ? endTime : null) +
//                ", notes=" + (notes != null ? notes : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProblemAttemptDTO that = (UserProblemAttemptDTO) o;
        return attemptId == that.attemptId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attemptId);
    }
}
