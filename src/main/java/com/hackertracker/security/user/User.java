/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hackertracker.security.user;

import com.hackertracker.security.Schedule.Weekday;
import com.hackertracker.security.Schedule.WeekdayName;
import com.hackertracker.security.problem.Problem;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 *
 * @author danysigha
 */

@Entity
@Table(name="user")
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

    public User(int userId, String publicId, String firstName, String lastName, String userName, String password, String email, Role role) {
        this.userId = userId;
        this.publicId = publicId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public User() {}

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProblemAttempt> problemAttempts = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProblemPriority> problemPriorities = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCompletionPrediction> completionPredictions = new HashSet<>();


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_schedule_id")
    private UserSchedule userSchedule;


    // Methods to manage the relationship
    public void addAttempt(Problem problem, User user, byte difficultyRating, Date startTime, Date endTime, String notes) {
        UserProblemAttempt problemAttempt = new UserProblemAttempt(problem, this, difficultyRating, startTime, endTime, notes);
        problemAttempts.add(problemAttempt);
        problem.getProblemAttempts().add(problemAttempt);
    }

    public void removeAttempt(UserProblemAttempt attempt) {
        for (Iterator<UserProblemAttempt> iterator = problemAttempts.iterator(); iterator.hasNext();) {
            UserProblemAttempt nextAttempt = iterator.next();
            if (nextAttempt.getAttemptId() == attempt.getAttemptId()) {
                iterator.remove();
                attempt.getProblem().getProblemAttempts().remove(nextAttempt);
                nextAttempt.setProblem(null);
                nextAttempt.setUser(null);
                break; // Exit loop after finding and removing the tag
            }
        }
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
     * Get all predictions for this user's completion of the 150 questions
     * Note: This method must be called within a transaction context
     * to avoid LazyInitializationException
     *
     * @return List of UserCompletionPrediction associated with this user
     */
    public List<UserCompletionPrediction> getListCompletionPrediction() {
        return completionPredictions.stream().toList();
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


    // Methods to manage the relationship
    public void updateSchedule(String weekday, int newTargetCount) {
        switch ( WeekdayName.valueOf(weekday) ) {
            case WeekdayName.Monday -> userSchedule.getMonday().setTargetProblemCount(newTargetCount);
            case WeekdayName.Tuesday -> userSchedule.getTuesday().setTargetProblemCount(newTargetCount);
            case WeekdayName.Wednesday -> userSchedule.getWednesday().setTargetProblemCount(newTargetCount);
            case WeekdayName.Thursday -> userSchedule.getThursday().setTargetProblemCount(newTargetCount);
            case WeekdayName.Friday -> userSchedule.getFriday().setTargetProblemCount(newTargetCount);
            case WeekdayName.Saturday -> userSchedule.getSaturday().setTargetProblemCount(newTargetCount);
            case WeekdayName.Sunday -> userSchedule.getSunday().setTargetProblemCount(newTargetCount);
        }
    }


    @Override
    public String toString() {
        return "User{" + "userId=" + userId +
                ", publicId=" + publicId +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", userName=" + userName +
                ", password=" + password +
                ", email=" + email + '}';
    }
}

//    public User() {
//    }

//    public User(String firstName,
//            String lastName,
//            String userName,
//            String password,
//            String email) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.userName = userName;
//        this.password = password;
//        this.email = email;
//    }
//
//    public User(int id,
//            String firstName,
//            String lastName,
//            String userName,
//            String password,
//            String email) {
//        this.id = id;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.userName = userName;
//        this.password = password;
//        this.email = email;
//    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }



//    public void setPassword(String password) {
//        this.password = password;
//    }

//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }





///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.hackertracker.security.user;
//
//import com.hackertracker.security.Schedule.Weekday;
//import com.hackertracker.security.Schedule.WeekdayName;
//import com.hackertracker.security.problem.Problem;
//import jakarta.persistence.*;
//import lombok.*;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.*;
//
///**
// *
// * @author danysigha
// */
//
//@Entity
//@Table(name="user")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class User implements UserDetails {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_id")
//    private int userId;
//    @Column(name = "public_id", nullable = false, unique = true, length = 36)
//    private String publicId;
//    @Column(name = "first_name", nullable = false)
//    private String firstName;
//    @Column(name = "last_name", nullable = false)
//    private String lastName;
//    @Column(name = "user_name", nullable = false)
//    private String userName;
//    @Column(name = "password", nullable = false)
//    private String password;
//    @Column(name = "email", nullable = false)
//    private String email;
//    @Enumerated(EnumType.STRING)
//    private Role role;
//
//    @PrePersist
//    protected void onCreate() {
//        if (publicId == null) {
//            publicId = UUID.randomUUID().toString();
//        }
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role.name()));
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return userName;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<UserProblemAttempt> problemAttempts = new HashSet<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<UserProblemPriority> problemPriorities = new HashSet<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<UserCompletionPrediction> completionPredictions = new HashSet<>();
//
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_schedule_id")
//    private UserSchedule userSchedule;
//
//
//    // Methods to manage the relationship
//    public void addAttempt(Problem problem, User user, byte difficultyRating, Date startTime, Date endTime, String notes) {
//        UserProblemAttempt problemAttempt = new UserProblemAttempt(problem, this, difficultyRating, startTime, endTime, notes);
//        problemAttempts.add(problemAttempt);
//        problem.getProblemAttempts().add(problemAttempt);
//    }
//
//    public void removeAttempt(UserProblemAttempt attempt) {
//        for (Iterator<UserProblemAttempt> iterator = problemAttempts.iterator(); iterator.hasNext();) {
//            UserProblemAttempt nextAttempt = iterator.next();
//            if (nextAttempt.getAttemptId() == attempt.getAttemptId()) {
//                iterator.remove();
//                attempt.getProblem().getProblemAttempts().remove(nextAttempt);
//                nextAttempt.setProblem(null);
//                nextAttempt.setUser(null);
//                break; // Exit loop after finding and removing the tag
//            }
//        }
//    }
//
//    /**
//     * Get all priorities for this user's problems
//     * Note: This method must be called within a transaction context
//     * to avoid LazyInitializationException
//     *
//     * @return List of UserProblemPriority associated with this problem
//     */
//    public List<UserProblemPriority> getListProblemPriorities() {
//        return problemPriorities.stream().toList();
//    }
//
//    /**
//     * Get all predictions for this user's completion of the 150 questions
//     * Note: This method must be called within a transaction context
//     * to avoid LazyInitializationException
//     *
//     * @return List of UserCompletionPrediction associated with this user
//     */
//    public List<UserCompletionPrediction> getListCompletionPrediction() {
//        return completionPredictions.stream().toList();
//    }
//
//
//    /**
//     * Get all attempts for this user of the 150 questions
//     * Note: This method must be called within a transaction context
//     * to avoid LazyInitializationException
//     *
//     * @return List of UserProblemAttempt associated with this user
//     */
//    public List<UserProblemAttempt> getListAttempts() {
//        return problemAttempts.stream().toList();
//    }
//
//
//    // Methods to manage the relationship
//    public void updateSchedule(String weekday, int newTargetCount) {
//        switch ( WeekdayName.valueOf(weekday) ) {
//            case WeekdayName.Monday -> userSchedule.getMonday().setTargetProblemCount(newTargetCount);
//            case WeekdayName.Tuesday -> userSchedule.getTuesday().setTargetProblemCount(newTargetCount);
//            case WeekdayName.Wednesday -> userSchedule.getWednesday().setTargetProblemCount(newTargetCount);
//            case WeekdayName.Thursday -> userSchedule.getThursday().setTargetProblemCount(newTargetCount);
//            case WeekdayName.Friday -> userSchedule.getFriday().setTargetProblemCount(newTargetCount);
//            case WeekdayName.Saturday -> userSchedule.getSaturday().setTargetProblemCount(newTargetCount);
//            case WeekdayName.Sunday -> userSchedule.getSunday().setTargetProblemCount(newTargetCount);
//        }
//    }
//
////    public int getUserId() {
////        return userId;
////    }
////
////    public void setUserId(int userId) {
////        this.userId = userId;
////    }
//
//
//    @Override
//    public String toString() {
//        return "User{" + "userId=" + userId +
//                ", publicId=" + publicId +
//                ", firstName=" + firstName +
//                ", lastName=" + lastName +
//                ", userName=" + userName +
//                ", password=" + password +
//                ", email=" + email + '}';
//    }
//}
//
////    public User() {
////    }
//
////    public User(String firstName,
////            String lastName,
////            String userName,
////            String password,
////            String email) {
////        this.firstName = firstName;
////        this.lastName = lastName;
////        this.userName = userName;
////        this.password = password;
////        this.email = email;
////    }
////
////    public User(int id,
////            String firstName,
////            String lastName,
////            String userName,
////            String password,
////            String email) {
////        this.id = id;
////        this.firstName = firstName;
////        this.lastName = lastName;
////        this.userName = userName;
////        this.password = password;
////        this.email = email;
////    }
//
////    public int getId() {
////        return id;
////    }
////
////    public void setId(int id) {
////        this.id = id;
////    }
////
////    public String getFirstName() {
////        return firstName;
////    }
////
////    public void setFirstName(String firstName) {
////        this.firstName = firstName;
////    }
////
////    public String getLastName() {
////        return lastName;
////    }
////
////    public void setLastName(String lastName) {
////        this.lastName = lastName;
////    }
////
////    public String getUserName() {
////        return userName;
////    }
////
////    public void setUserName(String userName) {
////        this.userName = userName;
////    }
//
//
//
////    public void setPassword(String password) {
////        this.password = password;
////    }
//
////    public String getEmail() {
////        return email;
////    }
////
////    public void setEmail(String email) {
////        this.email = email;
////    }
//
//
//
//
