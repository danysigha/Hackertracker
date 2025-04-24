package com.hackertracker.security.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hackertracker.security.problem.Problem;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@NamedQuery(name = "challenge.orderByPriority", query="FROM UserProblemPriority where user=:user order by priorityScore desc")
@Entity
@Table(name="user_problem_priorities")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    @JsonBackReference
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    public int getPriorityId() {
        return priorityId;
    }

    public UserProblemPriority(int priorityId, double priorityScore, Date lastCalculation, Date lastAttempted, Problem problem, User user) {
        this.priorityId = priorityId;
        this.priorityScore = priorityScore;
        this.lastCalculation = lastCalculation;
        this.lastAttempted = lastAttempted;
        this.problem = problem;
        this.user = user;
    }

    public UserProblemPriority() {
    }

    public void setPriorityId(int priorityId) {
        this.priorityId = priorityId;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public Date getLastCalculation() {
        return lastCalculation;
    }

    public void setLastCalculation(Date lastCalculation) {
        this.lastCalculation = lastCalculation;
    }

    public Date getLastAttempted() {
        return lastAttempted;
    }

    public void setLastAttempted(Date lastAttempted) {
        this.lastAttempted = lastAttempted;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


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
