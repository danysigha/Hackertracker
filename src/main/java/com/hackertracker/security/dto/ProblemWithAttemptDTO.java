package com.hackertracker.security.dto;

import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.user.UserProblemAttempt;

public class ProblemWithAttemptDTO {
    private ProblemDTO problem;
    private UserProblemAttemptDTO attempt;

    // Default constructor
    public ProblemWithAttemptDTO() {}

    // Constructor from entities
    public ProblemWithAttemptDTO(Problem problem, UserProblemAttempt attempt) {
        this.problem = ProblemDTO.fromEntity(problem);
        this.attempt = UserProblemAttemptDTO.fromEntity(attempt);
    }

    // Getters and setters
    public ProblemDTO getProblem() { return problem; }
    public void setProblem(ProblemDTO problem) { this.problem = problem; }

    public UserProblemAttemptDTO getAttempt() { return attempt; }
    public void setAttempt(UserProblemAttemptDTO attempt) { this.attempt = attempt; }
}