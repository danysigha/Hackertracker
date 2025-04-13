package com.hackertracker.security.home;

/**
 *
 * @author danysigha
 */

import com.hackertracker.security.user.User;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
public class HomeController {

    @Autowired
    UserRegistrationValidator userRegistrationValidator;

    @Autowired
    AuthenticationService authenticationService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/register")
    public String register(ModelMap mp, User user) {
        mp.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String handleUserRegistration(@ModelAttribute User user,
                                         BindingResult bindingResult) {
        userRegistrationValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()) {
            return "register";
        }

        RegisterRequest registerRequest = new RegisterRequest(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getPassword());
        authenticationService.register(registerRequest);
        return "userSuccess";
    }

    @GetMapping("/login")
    public String login(ModelMap mp, User user) {
        mp.addAttribute("user", user);
        return "login";
    }

    @PostMapping("/login")
    public String handleUserLogin(@ModelAttribute User user,
                        BindingResult bindingResult) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(user.getUsername(), user.getPassword());
        authenticationService.authenticate(authenticationRequest);
        return "userSuccess";
    }
}



