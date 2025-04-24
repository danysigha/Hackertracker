//package com.hackertracker.security.dto;
//
//import com.hackertracker.security.user.Role;
//import jakarta.persistence.Column;
//
//import java.util.List;
//import java.util.Objects;
//
//
//public class UserDTO {
//    private Integer userId;
//    private String publicId;
//    private String userName;
//    private String firstName;
//    private String lastName;
//    private String password;
//    private String email;
//    private Role role;
//    private List<UserProblemAttemptDTO> attempts;
//
////    public int getUserId() {
////        return userId;
////    }
////
////    public void setUserId(int userId) {
////        this.userId = userId;
////    }
//
//    public UserDTO(Integer userId, String publicId, String userName, String firstName, String lastName, String password, String email, Role role, List<UserProblemAttemptDTO> attempts) {
//        this.userId = userId;
//        this.publicId = publicId;
//        this.userName = userName;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.password = password;
//        this.email = email;
//        this.role = role;
//        this.attempts = attempts;
//    }
//
//    public UserDTO() {
//    }
//
//    public void setUserId(Integer userId) {
//        this.userId = userId;
//    }
//
//    public void setPublicId(String publicId) {
//        this.publicId = publicId;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public void setAttempts(List<UserProblemAttemptDTO> attempts) {
//        this.attempts = attempts;
//    }
//
//    public Integer getUserId() {
//        return userId;
//    }
//
//    public String getPublicId() {
//        return publicId;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }
//
//    public List<UserProblemAttemptDTO> getAttempts() {
//        return attempts;
//    }
//
//    @Override
//    public String toString() {
//        return "UserDTO{" +
//                "userId=" + userId + ", " +
//                ", publicId=" + publicId +
//                ", firstName=" + firstName +
//                ", lastName=" + lastName +
//                ", userName=" + userName +
//                ", password=" + password +
//                ", email=" + email +
//                ", role=" + role +
//                "} ";
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        UserDTO user = (UserDTO) o;
//        return userId == user.userId;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(userId);
//    }
//}
