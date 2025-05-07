package com.hackertracker.security.problem;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import com.hackertracker.security.tag.Tag;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.util.Objects;

@Entity
@Table(name="tag_problems")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TagProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_problem_id")
    private int tagProblemId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    @IndexedEmbedded
    private Tag tag;

    TagProblem(Problem problem, Tag tag) {
        this.problem = problem;
        this.tag = tag;
    }

    public TagProblem() {
    }

    public TagProblem(int tagProblemId, Problem problem, Tag tag) {
        this.tagProblemId = tagProblemId;
        this.problem = problem;
        this.tag = tag;
    }

    public int getTagProblemId() {
        return tagProblemId;
    }

    public void setTagProblemId(int tagProblemId) {
        this.tagProblemId = tagProblemId;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
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