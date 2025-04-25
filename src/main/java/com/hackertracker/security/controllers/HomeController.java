package com.hackertracker.security.controllers;

/**
 *
 * @author danysigha
 */

import com.hackertracker.security.auth.AuthenticationResponse;
import jakarta.servlet.http.HttpServletResponse;
import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.dao.UserProblemPriorityDAO;
import com.hackertracker.security.user.User;
import com.hackertracker.security.validator.UserLoginValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import com.hackertracker.security.validator.UserRegistrationValidator;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.hackertracker.security.auth.RegisterRequest;
import com.hackertracker.security.auth.AuthenticationService;
import com.hackertracker.security.auth.AuthenticationRequest;
import com.hackertracker.security.config.JwtService;
import jakarta.servlet.http.Cookie;

@Controller
public class HomeController {

    private final UserRegistrationValidator userRegistrationValidator;
    private final UserLoginValidator userLoginValidator;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserDAO userDao;
    private final UserProblemPriorityDAO userProblemPriorityDao;

    public HomeController(UserLoginValidator userLoginValidator, AuthenticationService authenticationService, JwtService jwtService, UserDAO userDao, UserProblemPriorityDAO userProblemPriorityDao, UserRegistrationValidator userRegistrationValidator) {
        this.userLoginValidator = userLoginValidator;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userDao = userDao;
        this.userProblemPriorityDao = userProblemPriorityDao;
        this.userRegistrationValidator = userRegistrationValidator;
    }


    @GetMapping("/")
    public String home() {
        return "landingPage";
    }

    @GetMapping("/register")
    public String register(ModelMap mp, User user) {
        mp.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String handleUserRegistration(ModelMap model, @ModelAttribute User user,
                                         BindingResult bindingResult) {
        userRegistrationValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "register";
        }

        RegisterRequest registerRequest = new RegisterRequest(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getPassword());
        authenticationService.register(registerRequest);

        model.addAttribute("user", user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(ModelMap mp, User user) {
        mp.addAttribute("user", user);
        return "login";
    }

    @PostMapping("/login")
    public String handleUserLogin(ModelMap model, @ModelAttribute User user,
                                  BindingResult bindingResult, HttpServletResponse response) {

        userLoginValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(user.getUserName(), user.getPassword());
            AuthenticationResponse authResponse = authenticationService.authenticate(authenticationRequest);

            String token = authResponse.getToken(); // Assuming your authenticate method returns the token

            // Or set it as a cookie
            Cookie jwtCookie = new Cookie("jwtToken", token);
            jwtCookie.setPath("/");
            jwtCookie.setHttpOnly(false); // Allow JavaScript to access
            jwtCookie.setMaxAge(86400); // 1 day in seconds
            response.addCookie(jwtCookie);

            model.addAttribute("user", user);

            return "redirect:/dashboard";
        } catch (Exception e) {
            bindingResult.reject("invalid.credentials", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal User user) {

        // Delete the JWT cookie
        Cookie cookie = new Cookie("jwtToken", "");  // Empty value
        cookie.setPath("/");
        cookie.setMaxAge(0);  // Expires immediately
        cookie.setHttpOnly(false);
        response.addCookie(cookie);

        User myUser = userDao.getUserByUserName(user.getUserName());
        myUser.incrementTokenVersion();
        userDao.updateUser(myUser);

        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request) {
        // You could check authentication here if needed
        return "dashboard"; // Return the dashboard view
    }
}