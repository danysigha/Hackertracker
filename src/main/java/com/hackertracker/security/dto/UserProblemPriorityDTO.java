package com.hackertracker.security.dto;

import java.util.Date;
import java.util.Objects;


public class UserProblemPriorityDTO {
    private int priorityId;
    private double priorityScore;
    private Date lastCalculation;
    private Date lastAttempted;
    private ProblemDTO problemDto;
    private UserDTO userDto;

    public UserProblemPriorityDTO() {
    }

    public UserProblemPriorityDTO(int priorityId, double priorityScore, Date lastCalculation, Date lastAttempted, ProblemDTO problemDto, UserDTO userDto) {
        this.priorityId = priorityId;
        this.priorityScore = priorityScore;
        this.lastCalculation = lastCalculation;
        this.lastAttempted = lastAttempted;
        this.problemDto = problemDto;
        this.userDto = userDto;
    }

    public int getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(int priorityId) {
        this.priorityId = priorityId;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public Date getLastCalculation() {
        return lastCalculation;
    }

    public void setLastCalculation(Date lastCalculation) {
        this.lastCalculation = lastCalculation;
    }

    public Date getLastAttempted() {
        return lastAttempted;
    }

    public void setLastAttempted(Date lastAttempted) {
        this.lastAttempted = lastAttempted;
    }

    public ProblemDTO getProblemDto() {
        return problemDto;
    }

    public void setProblemDto(ProblemDTO problemDto) {
        this.problemDto = problemDto;
    }

    public UserDTO getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDTO userDto) {
        this.userDto = userDto;
    }

    @Override
    public String toString() {
        return "UserProblemPriorityDTO{" +
                "priorityId=" + priorityId +
                ", problemId=" + (problemDto != null ? problemDto.getProblemId() : null) +
                ", userId=" + (userDto != null ? userDto.getUserId() : null) +
                "priorityScore=" + priorityScore +
                ", lastCalculation=" + (lastCalculation != null ? lastCalculation : null) +
                ", lastAttempted=" + (lastAttempted != null ? lastAttempted : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProblemPriorityDTO that = (UserProblemPriorityDTO) o;
        return priorityId == that.priorityId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(priorityId);
    }
}
