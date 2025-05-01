package com.hackertracker.security.controllers;

import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.dao.UserScheduleDAO;
import com.hackertracker.security.dto.ProgressStatsDTO;
import com.hackertracker.security.user.ProgressStatsService;
import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserSchedule;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

//        System.out.println("INSIDE ProgressController. Here is the schedule\n\n");
        if(userSchedule == null) {
//            System.out.println("No schedule passed. Using the userScheduleDao\n\n");
            schedule = userScheduleDao.getScheduleByUser(userDao.getUserByUserName(user.getUsername()));
        } else {
            schedule = userSchedule;
        }
//        System.out.println("Schedule is null ? " + schedule.toString());
        return progressStatsService.createProgressStats(schedule);
    }
}