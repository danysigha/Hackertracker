package com.hackertracker.security.topic;

import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.problem.TagProblem;
import com.hackertracker.security.problem.TopicProblem;
import com.hackertracker.security.tag.Tag;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

//import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
//import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
//import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

@Entity
@Table(name = "topic")

//@Indexed
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private byte topicId;

    @Column(name = "topic_name", unique = true, nullable = false)
//  @KeywordField
    private String topicName;

    @Column(name = "topic_rank", nullable = false)
    private byte topicRank;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TopicProblem> problemTopics = new HashSet<>();

    /**
     * Get all problems for this tag as a List
     * Note: This method must be called within a transaction context
     * to avoid LazyInitializationException
     *
     * @return List of problems associated with this tag
     */
    public List<Problem> getListProblems() {
        return problemTopics.stream()
                .map(TopicProblem::getProblem)
                .collect(Collectors.toList());
    }

    public Topic(byte topicId, String topicName, byte topicRank, Set<TopicProblem> problemTopics) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.topicRank = topicRank;
        this.problemTopics = problemTopics;
    }

    public Topic() {
    }

    public byte getTopicId() {
        return topicId;
    }

    public void setTopicId(byte topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public byte getTopicRank() {
        return topicRank;
    }

    public void setTopicRank(byte topicRank) {
        this.topicRank = topicRank;
    }

    public Set<TopicProblem> getProblemTopics() {
        return problemTopics;
    }

    public void setProblemTopics(Set<TopicProblem> problemTopics) {
        this.problemTopics = problemTopics;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "topicId=" + topicId + ", " +
                "topicName=" + topicName +
                ", topicRank=" + (topicRank != 0 ? topicRank : null) +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return topicId == topic.topicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicId);
    }
}
