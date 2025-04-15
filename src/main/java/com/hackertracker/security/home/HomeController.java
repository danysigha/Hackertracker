package com.hackertracker.security.home;

/**
 *
 * @author danysigha
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.dao.UserProblemPriorityDAO;
import com.hackertracker.security.dto.ProblemDTO;
import com.hackertracker.security.dto.UserDTO;
import com.hackertracker.security.dto.UserProblemPriorityDTO;
import com.hackertracker.security.dto.UserProblemService;
import com.hackertracker.security.problem.UserProblemPriorityService;
import com.hackertracker.security.user.User;
import com.hackertracker.security.validator.UserLoginValidator;
import jakarta.servlet.http.HttpServletRequest;
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
import com.hackertracker.security.config.JwtService;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    UserRegistrationValidator userRegistrationValidator;

    @Autowired
    UserLoginValidator userLoginValidator;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserDAO userDao;

    @Autowired
    UserProblemPriorityDAO userProblemPriorityDao;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

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
    public String handleUserRegistration(@ModelAttribute User user,
                                         BindingResult bindingResult) {
        userRegistrationValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()) {
            return "register";
        }

        RegisterRequest registerRequest = new RegisterRequest(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getPassword());
        authenticationService.register(registerRequest);
        return "home";
    }

    @GetMapping("/login")
    public String login(ModelMap mp, User user) {
        mp.addAttribute("user", user);
        return "login";
    }

//    @PostMapping("/login")
//    public String handleUserLogin(@ModelAttribute User user,
//                                  BindingResult bindingResult) {
//
//        userLoginValidator.validate(user, bindingResult);
//
//        if(bindingResult.hasErrors()) {
//            return "login";
//        }
//
//        try {
//            AuthenticationRequest authenticationRequest = new AuthenticationRequest(user.getUserName(), user.getPassword());
//            authenticationService.authenticate(authenticationRequest);
//
//            return "home";
//        } catch (Exception e) {
//            bindingResult.reject("invalid.credentials", "Invalid username or password");
//            return "login";
//        }
//    }

    @PostMapping("/login")
    public String handleUserLogin(ModelMap model, @ModelAttribute User user,
                                  BindingResult bindingResult) {
        System.out.println("Login attempt for user: " + user.getUserName());

        userLoginValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()) {
            System.out.println("Validation errors: " + bindingResult.getAllErrors());
            return "login";
        }

        try {
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(user.getUserName(), user.getPassword());
//            System.out.println("Attempting authentication for: " + user.getUserName());
            authenticationService.authenticate(authenticationRequest);
//            System.out.println("Authentication successful for: " + user.getUserName());

            // Get the username from the token
            String username = user.getUsername();
//            System.out.println("Username: " + username);

            // Get the user from the database
            User myUser = userDao.getUserByUserName(username);
//            System.out.println("User retrieved from DB: " + (myUser != null ? myUser.getUserName() : "null"));

            // Add the user to the model
            model.addAttribute("user", myUser);

//            List<ProblemDTO> problemDtos = priorityService.getPrioritizedProblemsForUser(myUser); // works!
//            System.out.println("Problems retrieved: " + (problemDtos != null ? problemDtos.size() : "null")); // works
            List<UserProblemPriorityDTO> priorities = userProblemPriorityDao.findByUserOrderByPriorityScoreDesc(myUser);

            model.addAttribute("priorities", priorities);

            return "home";

        } catch (Exception e) {
//            e.printStackTrace();
            bindingResult.reject("invalid.credentials", "Invalid username or password");
            return "login";
        }
    }

//    @GetMapping("/home")
//    public String showHome(ModelMap model, HttpServletRequest request) {
//        // Get the JWT token from the request
//        String token = extractJwtFromRequest(request);
//
//        // Get the username from the token
//        String username = jwtService.extractUsername(token);
//
//        // Get the user from the database
//        UserDTO userDto = userDao.getUserDtoByUserName(username);
//
//        // Add the user to the model
//        model.addAttribute("user", userDto);
//
//        logger.debug("User! ", userDto);
//
//        User user = new User(userDto.getUserId(), userDto.getPublicId(), userDto.getFirstName(), userDto.getLastName(),
//                userDto.getUserName(), userDto.getPassword(), userDto.getEmail(), userDto.getRole());
//
//        // Fetch the problems directly and add to model
////        List<UserProblemPriorityDTO> priorities = userProblemPriorityDao.findByUserOrderByPriorityScoreDesc(user);
//
//        List<ProblemDTO> problemDtos =  priorityService.getPrioritizedProblemsForUser(user);
//        model.addAttribute("problemsDtos", problemDtos);
//
//        logger.debug("Problems! ", problemDtos);
//
//        return "home";
//    }
//
//    private String extractJwtFromRequest(HttpServletRequest request) {
//        // This would depend on how you're sending the token
//        // Common approach: from Authorization header
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//
//        return null;
//    }
}




//package com.hackertracker.security.home;
//
///**
// *
// * @author danysigha
// */
//
//import com.hackertracker.security.dto.UserDTO;
//import com.hackertracker.security.validator.UserLoginValidator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.validation.BindingResult;
//import com.hackertracker.security.validator.UserRegistrationValidator;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import com.hackertracker.security.auth.RegisterRequest;
//import com.hackertracker.security.auth.AuthenticationService;
//import com.hackertracker.security.auth.AuthenticationRequest;
//
//@Controller
//public class HomeController {
//
//    @Autowired
//    UserRegistrationValidator userRegistrationValidator;
//
//    @Autowired
//    UserLoginValidator userLoginValidator;
//
//    @Autowired
//    AuthenticationService authenticationService;
//
//    @GetMapping("/")
//    public String home() {
//        return "landingPage";
//    }
//
//    @GetMapping("/register")
//    public String register(ModelMap mp) {
//        UserDTO userDto = new UserDTO();
//        mp.addAttribute("userDto", userDto);
//        return "register";
//    }
//
//    @PostMapping("/register")
//    public String handleUserRegistration(@ModelAttribute("userDto") UserDTO userDto,
//                                         BindingResult bindingResult) {
//        userRegistrationValidator.validate(userDto, bindingResult);
//
//        if(bindingResult.hasErrors()) {
//            return "register";
//        }
//
//        RegisterRequest registerRequest = new RegisterRequest(userDto.getFirstName(), userDto.getLastName(), userDto.getUserName(), userDto.getEmail(), userDto.getPassword());
//        authenticationService.register(registerRequest);
//        return "home";
//    }
//
//    @GetMapping("/login")
//    public String login(ModelMap mp) {
//        UserDTO userDto = new UserDTO();
//        mp.addAttribute("userDto", userDto);
//        return "login";
//    }
//
//    @PostMapping("/login")
//    public String handleUserLogin(@ModelAttribute("userDto") UserDTO userDto,
//                        BindingResult bindingResult) {
//
//        userLoginValidator.validate(userDto, bindingResult);
//
//        if(bindingResult.hasErrors()) {
//            return "login";
//        }
//
//        try {
//            AuthenticationRequest authenticationRequest = new AuthenticationRequest(userDto.getUserName(), userDto.getPassword());
//            authenticationService.authenticate(authenticationRequest);
//            return "home";
//        } catch (Exception e) {
//            bindingResult.reject("invalid.credentials", "Invalid username or password");
//            return "login";
//        }
//    }
//}
//
//
//
