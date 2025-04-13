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
public class UserCompletionPredictionDTO {
    private int predictionId;
    private Date predictionDate;
    private Date predictedCompletionDate;
    private UserDTO userDto;

    @Override
    public String toString() {
        return "UserCompletionPrediction{" +
                "predictionId=" + predictionId +
                ", predictionDate=" + (predictionDate != null ? predictionDate : null) +
                ", predictedCompletionDate=" + (predictedCompletionDate != null ? predictedCompletionDate : null) +
                ", userId=" + (userDto != null ? userDto.getUserId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCompletionPredictionDTO that = (UserCompletionPredictionDTO) o;
        return predictionId == that.predictionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(predictionId);
    }
}