package com.hackertracker.security.controllers;

import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserProblemAttempt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    private final UserDAO userDao;

    public CalendarController(UserDAO userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/update")
    public List<Map<String, Object>> updateCalendar(@AuthenticationPrincipal User user) {

        int userId = userDao.getUserByUserName(user.getUserName()).getUserId();

        User myUser = userDao.getUserByIdWithCollections(userId);

        Map<String, Integer> hourlyStats = new HashMap<>();

        // Create a formatter for ISO-8601 format limited to hour precision
        SimpleDateFormat hourFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH");
        hourFormatter.setTimeZone(TimeZone.getTimeZone("UTC")); // Use UTC or your preferred timezone

        for (UserProblemAttempt attempt : myUser.getListAttempts()) {
            // Get the Date object
            Date startTime = attempt.getStartTime();

            // Format to hour precision in ISO format
            String hourKey = hourFormatter.format(startTime);

            // Increment the count for this hour
            hourlyStats.put(hourKey, hourlyStats.getOrDefault(hourKey, 0) + 1);
        }

        // Convert to the array of objects format you need
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : hourlyStats.entrySet()) {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("date", entry.getKey());
            dataPoint.put("value", entry.getValue());
            result.add(dataPoint);
        }

        return result;
    }
}
