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
@Table(name="user_problem_priorities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProblemPriority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "priority_id")
    private int priorityId;

    @Column(name = "priority_score", nullable = false)
    private double priorityScore;

    @Column(name = "last_calculation", nullable = false)
    private Date lastCalculation;

    @Column(name = "last_attempted") // is this needed??
    private Date lastAttempted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public UserProblemPriority(Problem problem, User user, double priorityScore) {
        this.problem = problem;
        this.user = user;
        this.priorityScore = priorityScore;
    }

    @Override
    public String toString() {
        return "UserProblemPriority{" +
                "priorityId=" + priorityId +
                ", problemId=" + (problem != null ? problem.getProblemId() : null) +
                ", userId=" + (user != null ? user.getUserId() : null) +
                "priorityScore=" + priorityScore +
                ", lastCalculation=" + (lastCalculation != null ? lastCalculation : null) +
                ", lastAttempted=" + (lastAttempted != null ? lastAttempted : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProblemPriority that = (UserProblemPriority) o;
        return priorityId == that.priorityId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(priorityId);
    }
}
