package com.hackertracker.validator;

import com.hackertracker.user.User;
import org.springframework.validation.Errors;
import org.springframework.stereotype.Component;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserLoginValidator implements Validator {
    @Override
    public boolean supports(Class<?> type) {
        return User.class.isAssignableFrom(type);
    }

    @Override
    public void validate(Object command, Errors errors) {
        User user = (User) command;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "empty-user-name", "Please provide a user name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "empty-password", "Please provide a password");
    }
}
