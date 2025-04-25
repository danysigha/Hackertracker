package com.hackertracker.security.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hackertracker.security.problem.Problem;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="user_problem_attempts")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserProblemAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id", nullable = false)
    private int attemptId;

    @Column(name = "difficulty_rating", nullable = false)
    private byte difficultyRating;

    @Column(name = "notes")
    private String notes;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserProblemAttempt(Problem problem, User user, byte difficultyRating, Date startTime, Date endTime, String notes) {
        this.problem = problem;
        this.user = user;
        this.difficultyRating = difficultyRating;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
    }

    public UserProblemAttempt(int attemptId, byte difficultyRating, String notes, Date startTime, Date endTime, Problem problem, User user) {
        this.attemptId = attemptId;
        this.difficultyRating = difficultyRating;
        this.notes = notes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.problem = problem;
        this.user = user;
    }

    public UserProblemAttempt() {
    }

    public int getAttemptId() {
        return attemptId;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
