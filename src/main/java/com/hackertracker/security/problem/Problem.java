package com.hackertracker.security.problem;

import com.hackertracker.security.tag.Tag;
import com.hackertracker.security.topic.Topic;
import com.hackertracker.security.user.UserProblemAttempt;
import com.hackertracker.security.user.UserProblemPriority;
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
@Table(name="problem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Indexed
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id")
    private int problemId;

    @Column(name = "public_problem_id", unique = true, nullable = false)
//    @KeywordField
    private String publicProblemId;

    @Column(name = "question_title", nullable = false)
//    @FullTextField
    private String questionTitle;

    @Column(name = "difficulty_level", nullable = false)
    private String difficultyLevel;

    @Column(name = "page_url", nullable = false)
//    @FullTextField
    private String pageUrl;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TagProblem> problemTags = new HashSet<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TopicProblem> problemTopics = new HashSet<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProblemAttempt> problemAttempts = new HashSet<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProblemPriority> problemPriorities = new HashSet<>();

    // Normally you do not have to add or remove tags
    // Methods to manage the relationship
//    public void addTag(Tag tag) {
//        TagProblem problemTag = new TagProblem(this, tag);
//        problemTags.add(problemTag);
//        tag.getProblemTags().add(problemTag);
//    }
//
//    public void removeTag(Tag tag) {
//        for (Iterator<TagProblem> iterator = problemTags.iterator(); iterator.hasNext();) {
//            TagProblem problemTag = iterator.next();
//            if (problemTag.getTag().getTagId() == tag.getTagId()) {
//                iterator.remove();
//                tag.getProblemTags().remove(problemTag);
//                problemTag.setProblem(null);
//                problemTag.setTag(null);
//                break; // Exit loop after finding and removing the tag
//            }
//        }
//    }

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


    // Normally you do not have to add or remove topics

    // Methods to manage the relationship
//    public void addTopic(Topic topic) {
//        TopicProblem problemTopic = new TopicProblem(this, topic);
//        problemTopics.add(problemTopic);
//        topic.getProblemTopics().add(problemTopic);
//    }
//
//    public void removeTopic(Topic topic) {
//        for (Iterator<TopicProblem> iterator = problemTopics.iterator(); iterator.hasNext();) {
//            TopicProblem problemTopic = iterator.next();
//            if (problemTopic.getTopic().getTopicId() == topic.getTopicId()) {
//                iterator.remove();
//                topic.getProblemTopics().remove(problemTopic);
//                problemTopic.setProblem(null);
//                problemTopic.setTopic(null);
//                break; // Exit loop after finding and removing the tag
//            }
//        }
//    }

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
