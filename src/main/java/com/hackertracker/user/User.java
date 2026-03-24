package com.hackertracker.security.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.hackertracker.security.Schedule.WeekdayName;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Collection;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author danysigha
 */

@Entity
@Table(name="app_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    @Column(name = "public_id", nullable = false, unique = true, length = 36)
    private String publicId;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "token_version")
    private int tokenVersion = 0;
    @Column(name = "token_version_updated_at")
    private LocalDateTime tokenVersionUpdatedAt;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProblemAttempt> problemAttempts = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProblemPriority> problemPriorities = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProblemCompletion> problemCompletions = new HashSet<>();

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_schedule_id")
    private UserSchedule userSchedule;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_topic_id")
    private UserTopics topicRanks;

    public User(int userId, String publicId, String firstName, String lastName, String userName, String password, String email, Role role, UserSchedule schedule, UserTopics topicRanks) {
        this.userId = userId;
        this.publicId = publicId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.role = role;
        this.userSchedule = schedule;
        this.topicRanks = topicRanks;
    }

    public User() {}

    public int getTokenVersion() {
        return tokenVersion;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getLastName() {
        return lastName;
    }

    public Role getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public void incrementTokenVersion() {
        this.tokenVersion++;
        this.tokenVersionUpdatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public UserSchedule getUserSchedule() {
        return userSchedule;
    }

    public void setUserSchedule(UserSchedule userSchedule) {
        this.userSchedule = userSchedule;
    }

    public Set<UserProblemCompletion> getProblemCompletions() {
        return problemCompletions;
    }

    public void setProblemCompletions(Set<UserProblemCompletion> problemCompletions) {
        this.problemCompletions = problemCompletions;
    }

    public UserTopics getTopicRanks() {
        return topicRanks;
    }

    public void setTopicRanks(UserTopics topicRanks) {
        this.topicRanks = topicRanks;
    }

    @PrePersist
    protected void onCreate() {
        if (publicId == null) {
            publicId = UUID.randomUUID().toString();
        }
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return userName;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Set<UserProblemPriority> getProblemPriorities() {
        return problemPriorities;
    }

    public void setProblemPriorities(Set<UserProblemPriority> problemPriorities) {
        this.problemPriorities = problemPriorities;
    }

    public Set<UserProblemAttempt> getProblemAttempts() {
        return problemAttempts;
    }

    public void setProblemAttempts(Set<UserProblemAttempt> problemAttempts) {
        this.problemAttempts = problemAttempts;
    }

    /**
     * Get all priorities for this user's problems
     * Note: This method must be called within a transaction context
     * to avoid LazyInitializationException
     *
     * @return List of UserProblemPriority associated with this problem
     */
    public List<UserProblemPriority> getListProblemPriorities() {
        return problemPriorities.stream().toList();
    }

    /**
     * Get all attempts for this user of the 150 questions
     * Note: This method must be called within a transaction context
     * to avoid LazyInitializationException
     *
     * @return List of UserProblemAttempt associated with this user
     */
    public List<UserProblemAttempt> getListAttempts() {
        return problemAttempts.stream().toList();
    }

    /**
     * Get all problems completed by this user out of the 150 questions
     * Note: This method must be called within a transaction context
     * to avoid LazyInitializationException
     *
     * @return List of UserProblemCompletions associated with this user
     */
    public List<UserProblemCompletion> getListCompletions() {
        return problemCompletions.stream().toList();
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId +
                ", publicId=" + publicId +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", userName=" + userName +
                ", password=" + password +
                ", email=" + email +
                ", schedule=" + userSchedule +
                ", topicRanks=" + topicRanks +
                '}';
    }
}