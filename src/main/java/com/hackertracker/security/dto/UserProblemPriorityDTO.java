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
public class UserProblemPriorityDTO {
    private int priorityId;
    private double priorityScore;
    private Date lastCalculation;
    private Date lastAttempted;
    private ProblemDTO problemDto;
    private UserDTO userDto;

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
