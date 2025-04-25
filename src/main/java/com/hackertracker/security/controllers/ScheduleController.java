package com.hackertracker.security.controllers;

import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.dao.UserScheduleDAO;
import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserSchedule;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final UserScheduleDAO userScheduleDao;
    private final UserDAO userDao;

    public ScheduleController(UserScheduleDAO userScheduleDao, UserDAO userDao) {
        this.userScheduleDao = userScheduleDao;
        this.userDao = userDao;
    }

    @GetMapping("/get")
    public UserSchedule getUserSchedule(@AuthenticationPrincipal User user) {
        User myUser = userDao.getUserByUserName(user.getUserName());
        return userScheduleDao.getScheduleByUser(myUser);
    }

    @PostMapping("/update")
    public UserSchedule updateUserSchedule(
            @RequestParam(value = "monday") int mondayTargetCount,
            @RequestParam(value = "tuesday") int tuesdayTargetCount,
            @RequestParam(value = "wednesday") int wednesdayTargetCount,
            @RequestParam(value = "thursday") int thursdayTargetCount,
            @RequestParam(value = "friday") int fridayTargetCount,
            @RequestParam(value = "saturday") int saturdayTargetCount,
            @RequestParam(value = "sunday") int sundayTargetCount,
            @AuthenticationPrincipal User user
    ) {
        // Get existing user
        User myUser = userDao.getUserByUserName(user.getUserName());

        UserSchedule existingUserSchedule = userScheduleDao.getScheduleByUser(myUser);

        // Update fields
        existingUserSchedule.getMonday().setTargetProblemCount(mondayTargetCount);
        existingUserSchedule.getTuesday().setTargetProblemCount(tuesdayTargetCount);
        existingUserSchedule.getWednesday().setTargetProblemCount(wednesdayTargetCount);
        existingUserSchedule.getThursday().setTargetProblemCount(thursdayTargetCount);
        existingUserSchedule.getFriday().setTargetProblemCount(fridayTargetCount);
        existingUserSchedule.getSaturday().setTargetProblemCount(saturdayTargetCount);
        existingUserSchedule.getSunday().setTargetProblemCount(sundayTargetCount);

        // Save updates
        userScheduleDao.updateUserSchedule(existingUserSchedule);

        // Fetch and return updated schedule
        return userScheduleDao.getScheduleByUser(myUser);
    }

}
