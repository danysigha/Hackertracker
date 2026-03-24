package com.hackertracker.user;

import com.hackertracker.dao.ProblemDAO;
import com.hackertracker.dao.UserProblemAttemptDAO;
import com.hackertracker.dao.UserProblemCompletionDAO;
import com.hackertracker.dto.ProgressStatsDTO;
import com.hackertracker.dto.UserProblemCompletionDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProgressStatsService {
    private final ProblemDAO problemDao;
    private final UserProblemCompletionDAO userProblemCompletionDao;
    private UserProblemAttemptDAO userProblemAttemptDao;

    public ProgressStatsService(ProblemDAO problemDao, UserProblemCompletionDAO userProblemCompletionDao, UserProblemAttemptDAO userProblemAttemptDao) {
        this.problemDao = problemDao;
        this.userProblemCompletionDao = userProblemCompletionDao;
        this.userProblemAttemptDao = userProblemAttemptDao;
    }

    public ProgressStatsDTO createProgressStats(UserSchedule userSchedule, User user) {
        ProgressStatsDTO dto = new ProgressStatsDTO(userSchedule);

        // Populate with repository data
        dto.setNumberOfQuestions(problemDao.getNumberOfProblems());
        dto.setNumberOfCompletedQuestions(userProblemCompletionDao.getNumberOfProblemsCompletedByUserId(user.getUserId()));
        dto.setNumberOfEasyQuestions(problemDao.getNumberOfProblemsByDifficultyLevel("Easy"));
        dto.setNumberOfEasyCompletedQuestions(userProblemCompletionDao.getNumberOfProblemsByDifficultyLevelByUserId("Easy", user.getUserId()));
        dto.setNumberOfMediumQuestion(problemDao.getNumberOfProblemsByDifficultyLevel("Medium"));
        dto.setNumberOfMediumCompletedQuestions(userProblemCompletionDao.getNumberOfProblemsByDifficultyLevelByUserId("Medium", user.getUserId()));
        dto.setNumberOfHardQuestions(problemDao.getNumberOfProblemsByDifficultyLevel("Hard"));
        dto.setNumberOfHardCompletedQuestions(userProblemCompletionDao.getNumberOfProblemsByDifficultyLevelByUserId("Hard", user.getUserId()));
        dto.setNumberOfAttempts(userProblemAttemptDao.getNumberOfAttemptsByUserId(user.getUserId()));

        List<UserProblemCompletionDTO> upcDtos = new ArrayList<>();
        userProblemCompletionDao.getAllCompletedProblemsByUserId(user.getUserId()).forEach(
                upc -> upcDtos.add(UserProblemCompletionDTO.fromEntity(upc))
        );
        dto.setUserProblemCompletionDtos(upcDtos);

        return dto;
    }
}
