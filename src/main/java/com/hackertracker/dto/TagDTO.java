package com.hackertracker.security.dto;

import com.hackertracker.security.tag.Tag;
import com.hackertracker.security.topic.Topic;

public class TagDTO {
    private byte tagId;
    private String tagName;

    public TagDTO() {}

    // Static factory method to create from entity
    public static TagDTO fromEntity(Tag tag) {
        if (tag == null) return null;

        TagDTO dto = new TagDTO();
        dto.setTagId(tag.getTagId());
        dto.setTagName(tag.getTagName());
        return dto;
    }

    public byte getTagId() {
        return tagId;
    }

    public void setTagId(byte tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
