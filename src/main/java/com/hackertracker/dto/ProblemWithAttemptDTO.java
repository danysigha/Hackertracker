package com.hackertracker.security.dto;

import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.user.UserProblemAttempt;

public class ProblemWithAttemptDTO {
    private ProblemDTO problem;
    private UserProblemAttemptDTO attempt;
    private boolean completed;

    // Default constructor
    public ProblemWithAttemptDTO() {}

    // Constructor from entities
    public ProblemWithAttemptDTO(Problem problem, UserProblemAttempt attempt) {
        this.problem = ProblemDTO.fromEntity(problem);
        this.attempt = UserProblemAttemptDTO.fromEntity(attempt);
    }

    public ProblemWithAttemptDTO(Problem problem, UserProblemAttemptDTO attemptDto) {
        this.problem = ProblemDTO.fromEntity(problem);
        this.attempt = attemptDto;
    }

    // Getters and setters
    public ProblemDTO getProblem() { return problem; }
    public void setProblem(ProblemDTO problem) { this.problem = problem; }

    // Add getter and setter for completed
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public UserProblemAttemptDTO getAttempt() { return attempt; }
    public void setAttempt(UserProblemAttemptDTO attempt) { this.attempt = attempt; }
}