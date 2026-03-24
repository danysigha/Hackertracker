package com.hackertracker.security.dto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.hackertracker.security.user.UserSchedule;

public class UserScheduleDTO {
    private int userScheduleId;
    private List<Integer> schedule = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0));

    public UserScheduleDTO() {}

    // Static factory method to create from entity
    public static UserScheduleDTO fromEntity(UserSchedule userSchedule) {
        if (userSchedule == null) return null;

        UserScheduleDTO dto = new UserScheduleDTO();
        dto.setUserScheduleId(userSchedule.getUserScheduleId());
        dto.setSchedule(userSchedule.getSchedule());

        return dto;
    }

    public int getUserScheduleId() {
        return userScheduleId;
    }

    public void setUserScheduleId(int userScheduleId) {
        this.userScheduleId = userScheduleId;
    }

    public List<Integer> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<Integer> schedule) {
        this.schedule = schedule;
    }
}
