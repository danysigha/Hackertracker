package com.hackertracker.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProblemAttemptDTO {

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
