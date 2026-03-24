package com.hackertracker.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hackertracker.problem.Problem;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="user_problem_completion")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Indexed
public class UserProblemCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completion_id")
    private int completionId;

    @Column(name = "completion_date", nullable = false)
    private LocalDateTime completionDate;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    @IndexedEmbedded(includePaths = {"publicProblemId", "questionTitle"})
    private Problem problem;

    public UserProblemCompletion(int completionId, LocalDateTime completionDate, User user, Problem problem) {
        this.completionId = completionId;
        this.completionDate = completionDate;
        this.user = user;
        this.problem = problem;
    }

    public UserProblemCompletion() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public int getCompletionId() {
        return completionId;
    }

    public void setCompletionId(int completionId) {
        this.completionId = completionId;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    @Override
    public String toString() {
        return "UserProblemCompletion{" +
                "completionId=" + completionId +
                ", completionDate=" + (completionDate != null ? completionDate : null) +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", problemId=" + (problem != null ? problem.getProblemId(): null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProblemCompletion that = (UserProblemCompletion) o;
        return completionId == that.completionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(completionId);
    }
}
