package com.hackertracker.security.problem;

import com.hackertracker.security.tag.Tag;
import com.hackertracker.security.topic.Topic;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name="topic_problems")

public class TopicProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_problem_id")
    private int topicProblemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    TopicProblem(Problem problem, Topic topic) {
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