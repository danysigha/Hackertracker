package com.hackertracker.security.dto;

import com.hackertracker.security.topic.Topic;

public class TopicDTO {
    private byte topicId;
    private String topicName;
    private byte topicRank;

    public TopicDTO() {}

    // Static factory method to create from entity
    public static TopicDTO fromEntity(Topic topic) {
        if (topic == null) return null;

        TopicDTO dto = new TopicDTO();
        dto.setTopicId(topic.getTopicId());
        dto.setTopicName(topic.getTopicName());
        dto.setTopicRank(topic.getTopicRank());

        return dto;
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
}
