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
@Table(name="user_problem_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProblemAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private int attemptId;

    @Column(name = "difficulty_rating", nullable = false)
    private byte difficultyRating;

    @Column(name = "notes")
    private String notes;

    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "end_time", nullable = false)
    private Date endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    UserProblemAttempt(Problem problem, User user, byte difficultyRating, Date startTime, Date endTime, String notes) {
        this.problem = problem;
        this.user = user;
        this.difficultyRating = difficultyRating;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "UserProblemAttempt{" +
                "attemptId=" + attemptId +
                ", problemId=" + (problem != null ? problem.getProblemId() : null) +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", difficultyRating=" + (difficultyRating != 0 ? difficultyRating : null) +
                ", startTime=" + (startTime != null ? startTime : null) +
                ", endTime=" + (endTime != null ? endTime : null) +
                ", notes=" + (notes != null ? notes : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProblemAttempt that = (UserProblemAttempt) o;
        return attemptId == that.attemptId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attemptId);
    }
}
