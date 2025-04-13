package com.hackertracker.security.tag;

import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.problem.TagProblem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
//import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
//import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name="tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Indexed
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private byte tagId;

    @Column(name = "tag_name", nullable = false)
//    @FullTextField
    private String tagName;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TagProblem> problemTags = new HashSet<>();

    /**
     * Get all problems for this tag as a List
     * Note: This method must be called within a transaction context
     * to avoid LazyInitializationException
     *
     * @return List of problems associated with this tag
     */
    public List<Problem> getListProblems() {
        return problemTags.stream()
                .map(TagProblem::getProblem)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagId=" + tagId + ", " +
                "tagName=" + tagName +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return tagId == tag.tagId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId);
    }
}