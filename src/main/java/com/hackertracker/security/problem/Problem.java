package com.hackertracker.security.problem;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hackertracker.security.tag.Tag;
import com.hackertracker.security.topic.Topic;
import com.hackertracker.security.user.UserProblemAttempt;
import com.hackertracker.security.user.UserProblemCompletion;
import com.hackertracker.security.user.UserProblemPriority;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name="problem")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Indexed
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id")
    private int problemId;

    @Column(name = "public_problem_id", unique = true, nullable = false)
    @KeywordField
    private String publicProblemId;

    @Column(name = "question_title", nullable = false)
    @FullTextField(analyzer = "english")
    private String questionTitle;

    @Column(name = "difficulty_level", nullable = false)
    @KeywordField(normalizer = "lowercase", sortable = Sortable.YES)
    private String difficultyLevel;

    @Column(name = "page_url", nullable = false)
    @FullTextField
    private String pageUrl;

    @JsonManagedReference
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @IndexedEmbedded
    private Set<TagProblem> problemTags = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @IndexedEmbedded
    private Set<TopicProblem> problemTopics = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @IndexedEmbedded
    private Set<UserProblemAttempt> problemAttempts = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProblemPriority> problemPriorities = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProblemCompletion> problemCompletions = new HashSet<>();

    public Problem(int problemId, String publicProblemId, String questionTitle, String difficultyLevel, String pageUrl, Set<TagProblem> problemTags, Set<TopicProblem> problemTopics, Set<UserProblemAttempt> problemAttempts, Set<UserProblemPriority> problemPriorities) {
        this.problemId = problemId;
        this.publicProblemId = publicProblemId;
        this.questionTitle = questionTitle;
        this.difficultyLevel = difficultyLevel;
        this.pageUrl = pageUrl;
        this.problemTags = problemTags;
        this.problemTopics = problemTopics;
        this.problemAttempts = problemAttempts;
        this.problemPriorities = problemPriorities;
    }

    public Problem() {
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public String getPublicProblemId() {
        return publicProblemId;
    }

    public void setPublicProblemId(String publicProblemId) {
        this.publicProblemId = publicProblemId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public Set<TagProblem> getProblemTags() {
        return problemTags;
    }

    public void setProblemTags(Set<TagProblem> problemTags) {
        this.problemTags = problemTags;
    }

    public Set<TopicProblem> getProblemTopics() {
        return problemTopics;
    }

    public void setProblemTopics(Set<TopicProblem> problemTopics) {
        this.problemTopics = problemTopics;
    }

    public Set<UserProblemAttempt> getProblemAttempts() {
        return problemAttempts;
    }

    public void setProblemAttempts(Set<UserProblemAttempt> problemAttempts) {
        this.problemAttempts = problemAttempts;
    }

    public Set<UserProblemPriority> getProblemPriorities() {
        return problemPriorities;
    }

    public void setProblemPriorities(Set<UserProblemPriority> problemPriorities) {
        this.problemPriorities = problemPriorities;
    }

    /**
     * Get all tags for this problem
     * Note: This method must be called within a transaction context
     * to avoid LazyInitializationException
     *
     * @return Set of tags associated with this problem
     */
    public List<Tag> getListTags() {
        return problemTags.stream()
                .map(TagProblem::getTag)
                .collect(Collectors.toList());
    }


    /**
     * Get all topics for this problem
     * Note: This method must be called within a transaction context
     * to avoid LazyInitializationException
     *
     * @return List of topics associated with this problem
     */
    public List<Topic> getListTopics() {
        return problemTopics.stream()
                .map(TopicProblem::getTopic)
                .collect(Collectors.toList());
    }

    /**
     * Get all attempts for this problem
     * Note: This method must be called within a transaction context
     * to avoid LazyInitializationException
     *
     * @return List of UserProblemAttempts associated with this problem
     */
    public List<UserProblemAttempt> getListAttempts() {
        return problemAttempts.stream().toList();
    }

    /**
     * Get all priorities for this problem (one for each user)
     * Note: This method must be called within a transaction context
     * to avoid LazyInitializationException
     *
     * @return List of UserProblemPriority associated with this problem
     */
    public List<UserProblemPriority> getListProblemPriorities() {
        return problemPriorities.stream().toList();
    }

    // Add getter and setter
    public Set<UserProblemCompletion> getProblemCompletions() {
        return problemCompletions;
    }

    public void setProblemCompletions(Set<UserProblemCompletion> problemCompletions) {
        this.problemCompletions = problemCompletions;
    }

    // Add a helper method to check if problem is completed by a specific user
    public boolean isCompletedByUser(int userId) {
        return problemCompletions.stream()
                .anyMatch(completion -> completion.getUser().getUserId() == userId);
    }

    // Add a helper method to get completion for a specific user
    public Optional<UserProblemCompletion> getCompletionByUser(int userId) {
        return problemCompletions.stream()
                .filter(completion -> completion.getUser().getUserId() == userId)
                .findFirst();
    }

    @Override
    public String toString() {
        return "Problem{" +
                "problemId=" + problemId + ", " +
                "publicProblemId=" + publicProblemId +
                ", questionTitle=" + questionTitle + "\n" +
                ", difficultyLevel=" + difficultyLevel + "\n" +
                ", pageUrl=" + pageUrl + "\n" +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Problem problem = (Problem) o;
        return problemId == problem.problemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemId);
    }
}
