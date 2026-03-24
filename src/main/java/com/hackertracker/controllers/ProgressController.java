package com.hackertracker.controllers;

import com.hackertracker.dao.UserDAO;
import com.hackertracker.dao.UserScheduleDAO;
import com.hackertracker.dto.ProgressStatsDTO;
import com.hackertracker.user.ProgressStatsService;
import com.hackertracker.user.User;
import com.hackertracker.user.UserSchedule;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    private final ProgressStatsService progressStatsService;
    private final UserScheduleDAO userScheduleDao;
    private final UserDAO userDao;

    public ProgressController(ProgressStatsService progressStatsService, UserScheduleDAO userScheduleDao, UserDAO userDao) {
        this.progressStatsService = progressStatsService;
        this.userScheduleDao = userScheduleDao;
        this.userDao = userDao;
    }

    @GetMapping("/stats")
    public ProgressStatsDTO getProgressStats(
            @RequestParam(value = "userSchedule", required = false) UserSchedule userSchedule,
            @AuthenticationPrincipal User user
    ) {
//        UserSchedule schedule = new UserSchedule();
        UserSchedule schedule;
        User myUser = userDao.getUserByUserName(user.getUsername());

//        System.out.println("INSIDE ProgressController. Here is the schedule\n\n");
        if(userSchedule == null) {
//            System.out.println("No schedule passed. Using the userScheduleDao\n\n");
            schedule = userScheduleDao.getScheduleByUser(myUser);
        } else {
            schedule = userSchedule;
        }
//        System.out.println("Schedule is null ? " + schedule.toString());
        return progressStatsService.createProgressStats(schedule, myUser);
    }

    @PostMapping("/update-schedule")
    public void updateSchedule(
            @RequestParam(value = "userSchedule", required = true) List<Integer> userSchedule,
            @AuthenticationPrincipal User user
    ) {
        User myUser = userDao.getUserByUserName(user.getUserName());
        myUser.getUserSchedule().setSchedule(userSchedule);
        userScheduleDao.updateUserSchedule(myUser.getUserSchedule());
    }
}