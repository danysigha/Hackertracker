package com.hackertracker.security.Schedule;

import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserProblemAttempt;
import com.hackertracker.security.user.UserSchedule;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Objects;

@Entity
@Table(name="weekdays")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Weekday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekday_id")
    private int weekdayId;

    @Enumerated(EnumType.STRING)
    @Column(name = "weekday_name", nullable = false)
    private WeekdayName weekdayName;

    @Column(name = "target_problem_count", nullable = false)
    private int targetProblemCount;

    // Constructor with fields
    public Weekday(WeekdayName weekdayName, Integer targetProblemCount) {
        this.weekdayName = weekdayName;
        this.targetProblemCount = targetProblemCount;
    }

    public Weekday(int weekdayId, WeekdayName weekdayName, int targetProblemCount) {
        this.weekdayId = weekdayId;
        this.weekdayName = weekdayName;
        this.targetProblemCount = targetProblemCount;
    }

    public Weekday() {
    }

    public int getWeekdayId() {
        return weekdayId;
    }

    public void setWeekdayId(int weekdayId) {
        this.weekdayId = weekdayId;
    }

    public WeekdayName getWeekdayName() {
        return weekdayName;
    }

    public void setWeekdayName(WeekdayName weekdayName) {
        this.weekdayName = weekdayName;
    }

    public int getTargetProblemCount() {
        return targetProblemCount;
    }

    public void setTargetProblemCount(int targetProblemCount) {
        this.targetProblemCount = targetProblemCount;
    }

    @Override
    public String toString() {
        return "Weekday{" +
                "weekdayId=" + weekdayId +
                ", weekdayName=" + (weekdayName != null ? weekdayName : null) +
                ", targetProblemCount=" + (targetProblemCount != 0 ? targetProblemCount : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weekday that = (Weekday) o;
        return weekdayId == that.weekdayId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekdayId);
    }

}
