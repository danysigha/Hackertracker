package com.hackertracker.security.user;

import jakarta.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name="user_topics")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserTopics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_topic_id")
    private int userTopicId;

    @OneToOne(mappedBy = "topicRanks")
    private User user;

    @ElementCollection
    @CollectionTable(name = "topic_details", joinColumns = @JoinColumn(name = "user_topic_id"))
    @OrderColumn(name = "topic_position")
    @Column(name = "topic_rank")
    private List<Byte> topics;

    public UserTopics() {}

    public UserTopics(int userTopicId, User user, List<Byte> topics) {
        this.userTopicId = userTopicId;
        this.user = user;
        this.topics = topics;
    }

    public int getUserTopicId() {
        return userTopicId;
    }

    public void setUserTopicId(int userTopicId) {
        this.userTopicId = userTopicId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Byte> getTopics() {
        return topics;
    }

    public void setTopics(List<Byte> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "UserTopics{" +
                "userTopicId=" + userTopicId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", topics=" + topics +
                '}';
    }
}
