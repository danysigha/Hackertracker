package com.hackertracker.security.Schedule;

import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserProblemAttempt;
import com.hackertracker.security.user.UserSchedule;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Objects;

@Entity
@Table(name="weekdays")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
