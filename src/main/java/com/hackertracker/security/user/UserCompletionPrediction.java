package com.hackertracker.security.user;

import com.hackertracker.security.problem.Problem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="user_completion_predictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCompletionPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prediction_id")
    private int predictionId;

    @Column(name = "prediction_date", nullable = false)
    private Date predictionDate;

    @Column(name = "predicted_completion_date", nullable = false)
    private Date predictedCompletionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Override
    public String toString() {
        return "UserCompletionPrediction{" +
                "predictionId=" + predictionId +
                ", predictionDate=" + (predictionDate != null ? predictionDate : null) +
                ", predictedCompletionDate=" + (predictedCompletionDate != null ? predictedCompletionDate : null) +
                ", userId=" + (user != null ? user.getUserId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCompletionPrediction that = (UserCompletionPrediction) o;
        return predictionId == that.predictionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(predictionId);
    }
}
