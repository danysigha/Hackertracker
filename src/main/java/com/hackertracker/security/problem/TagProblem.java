package com.hackertracker.security.problem;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hackertracker.security.tag.Tag;

import java.util.Objects;

@Entity
@Table(name="tag_problems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_problem_id")
    private int tagProblemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    TagProblem(Problem problem, Tag tag) {
        this.problem = problem;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "TagProblem{" +
                "tagProblemId=" + tagProblemId +
                ", problemId=" + (problem != null ? problem.getProblemId() : null) +
                ", tagId=" + (tag != null ? tag.getTagId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagProblem that = (TagProblem) o;
        return tagProblemId == that.tagProblemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagProblemId);
    }
}