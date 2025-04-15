package com.hackertracker.security.dto;
import java.util.Objects;


public class TagDTO {

    private byte tagId;
    private String tagName;

    public TagDTO(byte tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public TagDTO() {
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

    @Override
    public String toString() {
        return "TagDTO{" +
                "tagId=" + tagId + ", " +
                "tagName=" + tagName +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDTO tag = (TagDTO) o;
        return tagId == tag.tagId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId);
    }

}