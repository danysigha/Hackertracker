/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hackertracker.security.validator;

import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 *
 * @author danysigha
 */
@Component
public class UserRegistrationValidator implements Validator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
    private static final String USERNAME_REGEX = "^(?=.*[a-zA-Z])[a-zA-Z0-9_-]{4,}$"; // At least 4 chars, no whitespace, not digits only

    private final UserDAO userDao;

    @Autowired
    public UserRegistrationValidator(UserDAO userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean supports(Class<?> type) {
        return User.class.isAssignableFrom(type);
    }

    @Override
    public void validate(Object command, Errors errors) {
        User user = (User) command;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "empty-first-name", "Please provide a first name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "empty-last-name", "Please provide a last name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "empty-user-name", "Please provide a user name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "empty-email", "Please provide an email address");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "empty-password", "Please provide a password");

        String email = user.getEmail();
        if (email != null && !email.matches(EMAIL_REGEX)) {
            errors.rejectValue("email", "email.invalid", "Please enter a valid email address.");
        } // Duplicate username check
        else if (userDao.getUserByEmail(email) != null) {
            errors.rejectValue("email", "email.duplicate", "This email is already taken. Please choose another one.");
        }

        String userName = user.getUserName();
        if (userName != null && !userName.matches(USERNAME_REGEX)) {
            errors.rejectValue("userName", "userName.invalid", "Username must be at least 4 characters long, contain at least one letter, and cannot contain spaces.");
        } // Duplicate username check
        else if (userDao.getUserByUserName(userName) != null) {
            errors.rejectValue("userName", "userName.duplicate", "This username is already taken. Please choose another one.");
        }

        String password = user.getPassword();
        if (password != null && !password.matches(PASSWORD_REGEX)) {
            errors.rejectValue("password", "password.invalid", "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }
    }
}
