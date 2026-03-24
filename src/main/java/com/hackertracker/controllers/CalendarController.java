package com.hackertracker.controllers;

import com.hackertracker.dao.UserDAO;
import com.hackertracker.user.User;
import com.hackertracker.user.UserProblemAttempt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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

        // Create a formatter for ISO-8601 format with timezone information
        // This will create strings like "2023-05-02T14:00:00Z" (UTC time)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00:00Z'");

        for (UserProblemAttempt attempt : myUser.getListAttempts()) {
            // Get the LocalDateTime from the attempt
            LocalDateTime time = null;

            if (attempt.getStartTime() != null) {
                time = attempt.getStartTime();
            } else if (attempt.getEndTime() != null) {
                time = attempt.getEndTime();
            } else {
                continue; // Skip if both times are null
            }

            // Format to hour precision in ISO format.
            // Times are expected to be in UTC because they are stored as such
            String hourKey = time.format(formatter);

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
