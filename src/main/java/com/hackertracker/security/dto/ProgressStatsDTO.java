package com.hackertracker.security.dto;

import com.hackertracker.security.dao.UserProblemAttemptDAO;
import com.hackertracker.security.user.UserSchedule;

public class ProgressStatsDTO {
    private int numberOfQuestions;
    private int numberOfCompletedQuestions;
    private int numberOfEasyQuestions;
    private int numberOfEasyCompletedQuestions;
    private int numberOfMediumQuestion;
    private int numberOfMediumCompletedQuestions;
    private int numberOfHardQuestions;
    private int numberOfHardCompletedQuestions;
    private int numberOfAttempts;
    private UserScheduleDTO userScheduleDto;

    public ProgressStatsDTO() {}

    public ProgressStatsDTO(UserSchedule userSchedule) {
        this.userScheduleDto = UserScheduleDTO.fromEntity(userSchedule);
    }

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public void setNumberOfAttempts(int numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getNumberOfCompletedQuestions() {
        return numberOfCompletedQuestions;
    }

    public void setNumberOfCompletedQuestions(int numberOfCompletedQuestions) {
        this.numberOfCompletedQuestions = numberOfCompletedQuestions;
    }

    public int getNumberOfEasyQuestions() {
        return numberOfEasyQuestions;
    }

    public void setNumberOfEasyQuestions(int numberOfEasyQuestions) {
        this.numberOfEasyQuestions = numberOfEasyQuestions;
    }

    public int getNumberOfMediumQuestion() {
        return numberOfMediumQuestion;
    }

    public void setNumberOfMediumQuestion(int numberOfMediumQuestion) {
        this.numberOfMediumQuestion = numberOfMediumQuestion;
    }

    public int getNumberOfEasyCompletedQuestions() {
        return numberOfEasyCompletedQuestions;
    }

    public void setNumberOfEasyCompletedQuestions(int numberOfEasyCompletedQuestions) {
        this.numberOfEasyCompletedQuestions = numberOfEasyCompletedQuestions;
    }

    public int getNumberOfMediumCompletedQuestions() {
        return numberOfMediumCompletedQuestions;
    }

    public void setNumberOfMediumCompletedQuestions(int numberOfMediumCompletedQuestions) {
        this.numberOfMediumCompletedQuestions = numberOfMediumCompletedQuestions;
    }

    public int getNumberOfHardQuestions() {
        return numberOfHardQuestions;
    }

    public void setNumberOfHardQuestions(int numberOfHardQuestions) {
        this.numberOfHardQuestions = numberOfHardQuestions;
    }

    public int getNumberOfHardCompletedQuestions() {
        return numberOfHardCompletedQuestions;
    }

    public void setNumberOfHardCompletedQuestions(int numberOfHardCompletedQuestions) {
        this.numberOfHardCompletedQuestions = numberOfHardCompletedQuestions;
    }

    public UserScheduleDTO getUserScheduleDto() {
        return userScheduleDto;
    }

    public void setUserScheduleDto(UserScheduleDTO userScheduleDto) {
        this.userScheduleDto = userScheduleDto;
    }
}
