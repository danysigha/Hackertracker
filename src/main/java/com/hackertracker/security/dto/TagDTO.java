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
public class TagDTO {

    private byte tagId;
    private String tagName;


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