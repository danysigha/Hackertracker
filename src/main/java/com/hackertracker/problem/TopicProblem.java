package com.hackertracker.problem;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hackertracker.topic.Topic;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import java.util.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

@Entity
@Table(name="topic_problems")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TopicProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_problem_id")
    private int topicProblemId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    @IndexedEmbedded
    private Topic topic;

    public TopicProblem(Problem problem, Topic topic) {
        this.problem = problem;
        this.topic = topic;
    }

    public TopicProblem(int topicProblemId, Problem problem, Topic topic) {
        this.topicProblemId = topicProblemId;
        this.problem = problem;
        this.topic = topic;
    }

    public TopicProblem() {
    }

    public int getTopicProblemId() {
        return topicProblemId;
    }

    public void setTopicProblemId(int topicProblemId) {
        this.topicProblemId = topicProblemId;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "TopicProblem{" +
                "topicProblemId=" + topicProblemId +
                ", problemId=" + (problem != null ? problem.getProblemId() : null) +
                ", topicId=" + (topic != null ? topic.getTopicId() : null) +
//                ", topicId=" + (topicRank != 0 ? topicRank : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicProblem that = (TopicProblem) o;
        return topicProblemId == that.topicProblemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicProblemId);
    }
}