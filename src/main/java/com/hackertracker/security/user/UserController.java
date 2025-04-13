///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.hackertracker.security.user;
//
//import com.hackertracker.security.dao.UserDAO;
//import com.hackertracker.security.validator.UserValidator;
//
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// *
// * @author danysigha
// */
//
//@Controller
//public class UserController {
//
//    private final UserService userService;
//
//    @Autowired
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Autowired
//    UserValidator userValidator;
//
//    @GetMapping
//    public List<User> getUsers() {
//        return userService.getStudents();
//    }
//
//    @GetMapping("/register")
//    public String register(ModelMap mp, User user) {
//        mp.addAttribute("user", user);
//        return "register";
//    }
//
//    @PostMapping("/register")
//    public String handleUserRegistration(@ModelAttribute User user, BindingResult bindingResult, UserDAO userDao) {
//        userValidator.validate(user, bindingResult);
//
//        if(bindingResult.hasErrors()) {
//            return "register";
//        }
//
//        userDao.saveUser(user);
//        return "userSuccess";
//    }
//
//}
