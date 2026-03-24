package com.hackertracker.problem;

import com.hackertracker.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "problem_history")
public class ProblemHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int problemHistoryId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(name = "view_timestamp")
    private LocalDateTime viewTimestamp;

    public ProblemHistory() {}

    ProblemHistory(User user, Problem problem, LocalDateTime viewTimestamp) {
        this.user = user;
        this.problem = problem;
        this.viewTimestamp = viewTimestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return problemHistoryId;
    }

    public void setId(int problemHistoryId) {
        this.problemHistoryId = problemHistoryId;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public LocalDateTime getViewTimestamp() {
        return viewTimestamp;
    }

    public void setViewTimestamp(LocalDateTime viewTimestamp) {
        this.viewTimestamp = viewTimestamp;
    }
}