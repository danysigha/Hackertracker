package com.hackertracker.security.problem;

import com.hackertracker.security.user.UserProblemAttempt;

public class ProblemWithAttempt {
    private Problem problem;
    private UserProblemAttempt attempt;

    // Default constructor
    public ProblemWithAttempt() {}

    // Constructor from entities
    public ProblemWithAttempt(Problem problem, UserProblemAttempt attempt) {
        this.problem = problem;
        this.attempt = attempt;
    }

    // Getters and setters
    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }

    public UserProblemAttempt getAttempt() { return attempt; }
    public void setAttempt(UserProblemAttempt attempt) { this.attempt = attempt; }
}