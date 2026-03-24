package com.hackertracker.user;

//import com.hackertracker.security.Schedule.Weekday;
//import com.hackertracker.security.Schedule.WeekdayName;
import jakarta.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="user_schedules")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_schedule_id")
    private int userScheduleId;

    @OneToOne(mappedBy = "userSchedule")
    private User user;

    @ElementCollection
    @CollectionTable(name = "schedule_details", joinColumns = @JoinColumn(name = "user_schedule_id"))
    @OrderColumn(name = "day_position")
    @Column(name = "target_problem_count")
    private List<Integer> schedule = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0));

    public UserSchedule(int userScheduleId, User user, List<Integer> schedule) {
        this.userScheduleId = userScheduleId;
        this.user = user;
        this.schedule = schedule;
    }

    public UserSchedule(User user) {
        this.user = user;
    }

    public UserSchedule() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSchedule that = (UserSchedule) o;
        return userScheduleId == that.userScheduleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userScheduleId);
    }

    @Override
    public String toString() {
        return "UserSchedule{" +
                "userScheduleId=" + userScheduleId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", monday= " + schedule.get(1) +
                ", tuesday= " + schedule.get(2) +
                ", wednesday= " + schedule.get(3) +
                ", thursday= " + schedule.get(4) +
                ", friday= " + schedule.get(5) +
                ", saturday= " + schedule.get(6) +
                ", sunday= " + schedule.get(0) +
                "}";
    }

}


//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "monday_id")
//    private Weekday monday;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "tuesday_id")
//    private Weekday tuesday;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "wednesday_id")
//    private Weekday wednesday;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "thursday_id")
//    private Weekday thursday;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "friday_id")
//    private Weekday friday;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "saturday_id")
//    private Weekday saturday;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "sunday_id")
//    private Weekday sunday;
//
//    @PrePersist
//    public void prePersist() {
//        if (monday == null) {
//            monday = new Weekday();
//            monday.setWeekdayName(WeekdayName.Monday);
//            monday.setTargetProblemCount(0);
//
//            tuesday = new Weekday();
//            tuesday.setWeekdayName(WeekdayName.Tuesday);
//            tuesday.setTargetProblemCount(0);
//
//            wednesday = new Weekday();
//            wednesday.setWeekdayName(WeekdayName.Wednesday);
//            wednesday.setTargetProblemCount(0);
//
//            thursday = new Weekday();
//            thursday.setWeekdayName(WeekdayName.Thursday);
//            thursday.setTargetProblemCount(0);
//
//            friday = new Weekday();
//            friday.setWeekdayName(WeekdayName.Friday);
//            friday.setTargetProblemCount(0);
//
//            saturday = new Weekday();
//            saturday.setWeekdayName(WeekdayName.Saturday);
//            saturday.setTargetProblemCount(0);
//
//            sunday = new Weekday();
//            sunday.setWeekdayName(WeekdayName.Sunday);
//            sunday.setTargetProblemCount(0);
//        }
//    }
//
//    public UserSchedule(int userScheduleId, User user, Weekday monday, Weekday tuesday, Weekday wednesday, Weekday thursday, Weekday friday, Weekday saturday, Weekday sunday) {
//        this.userScheduleId = userScheduleId;
//        this.user = user;
//        this.monday = monday;
//        this.tuesday = tuesday;
//        this.wednesday = wednesday;
//        this.thursday = thursday;
//        this.friday = friday;
//        this.saturday = saturday;
//        this.sunday = sunday;
//    }
//
//    public UserSchedule() {
//    }
//
//    public int getUserScheduleId() {
//        return userScheduleId;
//    }
//
//    public void setUserScheduleId(int userScheduleId) {
//        this.userScheduleId = userScheduleId;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public Weekday getMonday() {
//        return monday;
//    }
//
//    public void setMonday(Weekday monday) {
//        this.monday = monday;
//    }
//
//    public Weekday getTuesday() {
//        return tuesday;
//    }
//
//    public void setTuesday(Weekday tuesday) {
//        this.tuesday = tuesday;
//    }
//
//    public Weekday getWednesday() {
//        return wednesday;
//    }
//
//    public void setWednesday(Weekday wednesday) {
//        this.wednesday = wednesday;
//    }
//
//    public Weekday getThursday() {
//        return thursday;
//    }
//
//    public void setThursday(Weekday thursday) {
//        this.thursday = thursday;
//    }
//
//    public Weekday getFriday() {
//        return friday;
//    }
//
//    public void setFriday(Weekday friday) {
//        this.friday = friday;
//    }
//
//    public Weekday getSaturday() {
//        return saturday;
//    }
//
//    public void setSaturday(Weekday saturday) {
//        this.saturday = saturday;
//    }
//
//    public Weekday getSunday() {
//        return sunday;
//    }
//
//    public void setSunday(Weekday sunday) {
//        this.sunday = sunday;
//    }
//

