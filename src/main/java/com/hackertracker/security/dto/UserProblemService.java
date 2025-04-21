package com.hackertracker.security.dto;

import com.hackertracker.security.dao.ProblemDAO;
import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.dto.DTOMapper;
import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.dto.ProblemDTO;
import com.hackertracker.security.dto.TagDTO;
import com.hackertracker.security.dto.TopicDTO;
import com.hackertracker.security.user.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer to handle business logic and bridge between DAOs
 * This eliminates the circular dependency between ProblemDAO and UserDAO
 */
@Service
public class UserProblemService {

    private final SessionFactory sessionFactory;

    private final ProblemDAO problemDAO;

    private final DTOMapper dtoMapper;

    public UserProblemService(SessionFactory sessionFactory, ProblemDAO problemDAO, DTOMapper dtoMapper) {
        this.sessionFactory = sessionFactory;
        this.problemDAO = problemDAO;
        this.dtoMapper = dtoMapper;
    }

    /* ========================== PROBLEM RELATED METHODS ========================== */

    /**
     * Get a complete ProblemDTO with all related data
     */
    public ProblemDTO getProblemDto(Problem problem) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Problem mergedProblem = session.merge(problem);

                // Initialize the lazy collections within transaction
                mergedProblem.getListTopics().size(); // Force initialization
                if (mergedProblem.getListTags() != null) {
                    mergedProblem.getListTags().size(); // Force initialization
                }
                if (mergedProblem.getListAttempts() != null) {
                    mergedProblem.getListAttempts().size(); // Force initialization
                }
                if (mergedProblem.getListProblemPriorities() != null) {
                    mergedProblem.getListProblemPriorities().size(); // Force initialization
                }

                // Create the DTO within transaction
                ProblemDTO problemDto = dtoMapper.toProblemDtoBasic(mergedProblem);
                if (problemDto != null) {
                    problemDto.setAttempts(getProblemAttemptsInternal(mergedProblem, problemDto));
                }

                tx.commit();
                return problemDto;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Get all problems as DTOs with complete data
     */
    public List<ProblemDTO> getAllProblemDtos() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                List<Problem> problems = session.createQuery("FROM Problem", Problem.class).list();

                List<ProblemDTO> problemDtos = new ArrayList<>();
                for (Problem problem : problems) {
                    // Initialize the lazy collections within transaction
                    problem.getListTopics().size(); // Force initialization
                    if (problem.getListTags() != null) {
                        problem.getListTags().size(); // Force initialization
                    }
                    if (problem.getListAttempts() != null) {
                        problem.getListAttempts().size(); // Force initialization
                    }

                    // Create DTO within transaction
                    ProblemDTO dto = dtoMapper.toProblemDtoBasic(problem);
                    dto.setAttempts(getProblemAttemptsInternal(problem, dto));
                    problemDtos.add(dto);
                }

                tx.commit();
                return problemDtos;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Get problems by difficulty level as DTOs
     */
    public List<ProblemDTO> getProblemDtosByDifficultyLevel(String difficultyLevel) {
        return problemDAO.getProblemsByDifficultyLevel(difficultyLevel).stream()
                .map(this::getProblemDto)
                .collect(Collectors.toList());
    }

    /**
     * Search problems by title as DTOs
     */
    public List<ProblemDTO> searchProblemDtosByTitle(String title) {
        return problemDAO.searchProblemsByQuestionTitle(title).stream()
                .map(this::getProblemDto)
                .collect(Collectors.toList());
    }

    /**
     * Get a ProblemDTO by ID
     */
    public ProblemDTO getProblemDtoById(int id) {
        Problem problem = problemDAO.getProblemById(id);
        return problem != null ? getProblemDto(problem) : null;
    }

    /**
     * Get a ProblemDTO by public ID
     */
    public ProblemDTO getProblemDtoByPublicId(String publicId) {
        Problem problem = problemDAO.getProblemByPublicId(publicId);
        return problem != null ? getProblemDto(problem) : null;
    }

    /**
     * Get tags for a problem
     */
    public List<TagDTO> getProblemTags(Problem problem) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Problem mergedProblem = session.merge(problem);
                List<TagDTO> tags = dtoMapper.toTagDtoList(mergedProblem.getListTags());
                tx.commit();
                return tags;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Get topics for a problem
     */
    public List<TopicDTO> getProblemTopics(Problem problem) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Problem mergedProblem = session.merge(problem);
                List<TopicDTO> topics = dtoMapper.toTopicDtoList(mergedProblem.getListTopics());
                tx.commit();
                return topics;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Get attempts for a problem (for internal use with already merged problem)
     */
    private List<UserProblemAttemptDTO> getProblemAttemptsInternal(Problem mergedProblem, ProblemDTO problemDto) {
        return mergedProblem.getListAttempts().stream()
                .map(attempt -> dtoMapper.toUserProblemAttemptDto(attempt, problemDto))
                .collect(Collectors.toList());
    }

    /**
     * Get attempts for a problem (public API)
     */
    public List<UserProblemAttemptDTO> getProblemAttempts(Problem problem, ProblemDTO problemDto) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Problem mergedProblem = session.merge(problem);
                // Force initialization
                mergedProblem.getListAttempts().size();

                List<UserProblemAttemptDTO> attempts = getProblemAttemptsInternal(mergedProblem, problemDto);
                tx.commit();
                return attempts;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Get priorities for a problem
     */
    public List<UserProblemPriorityDTO> getProblemPriorities(Problem problem) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Problem mergedProblem = session.merge(problem);
                List<UserProblemPriorityDTO> priorities = mergedProblem.getListProblemPriorities().stream()
                        .map(priority -> {
                            ProblemDTO problemDto = getProblemDto(priority.getProblem());
                            UserDTO userDto = getUserDto(priority.getUser());
                            return dtoMapper.toUserProblemPriorityDto(priority, problemDto, userDto);
                        })
                        .collect(Collectors.toList());
                tx.commit();
                return priorities;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /* ========================== USER RELATED METHODS ========================== */

    /**
     * Get a complete UserDTO with all related data
     */
    public UserDTO getUserDto(User user) {
        UserDTO userDto = dtoMapper.toUserDtoBasic(user);
        if (userDto != null) {
            userDto.setAttempts(getUserAttempts(user));
        }
        return userDto;
    }

//    /**
//     * Get all users as DTOs
//     */
//    public List<UserDTO> getAllUserDtos() {
//        return userDAO.getAllUsers().stream()
//                .map(this::getUserDto)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Get a UserDTO by ID
//     */
//    public UserDTO getUserDtoById(int id) {
//        User user = userDAO.getUserById(id);
//        return user != null ? getUserDto(user) : null;
//    }
//
//    /**
//     * Get a UserDTO by public ID
//     */
//    public UserDTO getUserDtoByPublicId(String publicId) {
//        User user = userDAO.getUserByPublicId(publicId);
//        return user != null ? getUserDto(user) : null;
//    }

    /**
     * Get completion predictions for a user
     */
    public List<UserCompletionPredictionDTO> getCompletionPredictions(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                User mergedUser = session.merge(user);
                UserDTO userDto = dtoMapper.toUserDtoBasic(user);

                List<UserCompletionPredictionDTO> predictions = mergedUser.getListCompletionPrediction().stream()
                        .map(prediction -> dtoMapper.toUserCompletionPredictionDto(prediction, userDto))
                        .collect(Collectors.toList());

                tx.commit();
                return predictions;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Get attempts for a user
     */
    public List<UserProblemAttemptDTO> getUserAttempts(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                User mergedUser = session.merge(user);

                // Force initialization of user's attempts
                mergedUser.getListAttempts().size();

                List<UserProblemAttemptDTO> attempts = new ArrayList<>();

                for (UserProblemAttempt attempt : mergedUser.getListAttempts()) {
                    Problem problem = attempt.getProblem();
                    // Merge the problem to avoid LazyInitializationException
                    Problem mergedProblem = session.merge(problem);

                    // Force initialization of problem's collections
                    mergedProblem.getListTopics().size();
                    if (mergedProblem.getListTags() != null) {
                        mergedProblem.getListTags().size();
                    }

                    // Create DTOs within the transaction
                    ProblemDTO problemDto = new ProblemDTO(
                            mergedProblem.getProblemId(),
                            mergedProblem.getPublicProblemId(),
                            mergedProblem.getQuestionTitle(),
                            mergedProblem.getPageUrl(),
                            mergedProblem.getDifficultyLevel(),
                            dtoMapper.toTopicDtoList(mergedProblem.getListTopics()),
                            null
                    );

                    UserProblemAttemptDTO attemptDto = dtoMapper.toUserProblemAttemptDto(attempt, problemDto);
                    attempts.add(attemptDto);
                }

                tx.commit();
                return attempts;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Get priorities for a user
     */
    public List<UserProblemPriorityDTO> getUserPriorities(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                User mergedUser = session.merge(user);

                List<UserProblemPriorityDTO> priorities = mergedUser.getListProblemPriorities().stream()
                        .map(priority -> {
                            ProblemDTO problemDto = getProblemDto(priority.getProblem());
                            UserDTO userDto = dtoMapper.toUserDtoBasic(user);

                            return dtoMapper.toUserProblemPriorityDto(priority, problemDto, userDto);
                        })
                        .collect(Collectors.toList());

                tx.commit();
                return priorities;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}