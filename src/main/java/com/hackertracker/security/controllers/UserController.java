package com.hackertracker.security.controllers;

import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author danysigha
 */

@RequestMapping("/api/user")
@Controller
public class UserController {

    private final UserDAO userDao;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserDAO userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @ResponseBody
    @GetMapping("/read")
    public User getUserDetails(@AuthenticationPrincipal User user) {
        return userDao.getUserByIdWithCollections(userDao.getUserByUserName(user.getUserName()).getUserId());
    }

    @ResponseBody
    @PostMapping("/update")
    public User updateUserDetails(
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "lastName") String lastName,
            @RequestParam(value = "userName") String userName,
            @RequestParam(value = "password", required = false) String password,
            @AuthenticationPrincipal User user
    ) {
        // Get existing user
        User existingUser = userDao.getUserByUserName(user.getUserName());

        // Update fields
        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setUserName(userName);

        // Only update password if provided
        if (password != null && !password.isEmpty()) {
            // Encrypt password before saving
            String encryptedPassword = passwordEncoder.encode(password);
            existingUser.setPassword(encryptedPassword);
        }

        // Save updates
        userDao.updateUser(existingUser);

        // Fetch and return updated user (using new username if it changed)
        return userDao.getUserByUserName(userName);
    }

    @PostMapping("/delete")
    public String deleteUser(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal User user) {

        // Delete the JWT cookie
        Cookie cookie = new Cookie("jwtToken", "");  // Empty value
        cookie.setPath("/");
        cookie.setMaxAge(0);  // Expires immediately
        cookie.setHttpOnly(false);
        response.addCookie(cookie);

        User myUser = userDao.getUserByUserName(user.getUserName());
        myUser.incrementTokenVersion();
        userDao.updateUser(myUser);

        userDao.deleteUser(myUser.getUserId());
        return "redirect:/";
    }

}
