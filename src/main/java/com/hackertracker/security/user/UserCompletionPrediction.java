package com.hackertracker.security.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hackertracker.security.problem.Problem;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="user_completion_predictions")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserCompletionPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prediction_id")
    private int predictionId;

    @Column(name = "prediction_date", nullable = false)
    private Date predictionDate;

    @Column(name = "predicted_completion_date", nullable = false)
    private Date predictedCompletionDate;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public UserCompletionPrediction(int predictionId, Date predictedCompletionDate, Date predictionDate, User user) {
        this.predictionId = predictionId;
        this.predictedCompletionDate = predictedCompletionDate;
        this.predictionDate = predictionDate;
        this.user = user;
    }

    public UserCompletionPrediction() {
    }

    public int getPredictionId() {
        return predictionId;
    }

    public void setPredictionId(int predictionId) {
        this.predictionId = predictionId;
    }

    public Date getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(Date predictionDate) {
        this.predictionDate = predictionDate;
    }

    public Date getPredictedCompletionDate() {
        return predictedCompletionDate;
    }

    public void setPredictedCompletionDate(Date predictedCompletionDate) {
        this.predictedCompletionDate = predictedCompletionDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
