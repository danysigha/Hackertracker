package com.hackertracker.security.dto;

import java.util.Objects;

public class TopicDTO {

    private byte topicId;

    public TopicDTO() {
    }

    public TopicDTO(byte topicId, String topicName, byte topicRank) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.topicRank = topicRank;
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

    private String topicName;
    private byte topicRank;

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
        TopicDTO topic = (TopicDTO) o;
        return topicId == topic.topicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicId);
    }
}
