package com.hackertracker.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO {

    private byte topicId;
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
