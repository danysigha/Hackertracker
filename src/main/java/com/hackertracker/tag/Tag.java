package com.hackertracker.tag;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hackertracker.problem.Problem;
import com.hackertracker.problem.TagProblem;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

@Entity
@Table(name="tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private byte tagId;

    @Column(name = "tag_name", nullable = false)
    @KeywordField(normalizer = "lowercase", sortable = Sortable.YES)
    private String tagName;

    @JsonManagedReference
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

    public Tag(byte tagId, String tagName, Set<TagProblem> problemTags) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.problemTags = problemTags;
    }

    public Tag() {
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

    public Set<TagProblem> getProblemTags() {
        return problemTags;
    }

    public void setProblemTags(Set<TagProblem> problemTags) {
        this.problemTags = problemTags;
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