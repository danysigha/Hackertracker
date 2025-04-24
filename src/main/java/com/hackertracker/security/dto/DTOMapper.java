//package com.hackertracker.security.dto;
//
//import com.hackertracker.security.problem.Problem;
//import com.hackertracker.security.dto.ProblemDTO;
//import com.hackertracker.security.dto.TagDTO;
//import com.hackertracker.security.dto.TopicDTO;
//import com.hackertracker.security.user.*;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * A dedicated mapper class to convert between entity and DTO objects.
// * This eliminates the circular dependency between DAOs.
// */
//@Component
//public class DTOMapper {
//
//    /**
//     * Converts a Problem entity to a ProblemDTO without attempts data
//     * Note: All collections should be initialized before calling this method
//     */
//    public ProblemDTO toProblemDtoBasic(Problem problem) {
//        if (problem == null) return null;
//
//        // Since we've already initialized collections in the service layer,
//        // this should be safe to call
//        List<TopicDTO> topicDtos = null;
//        if (problem.getListTopics() != null) {
//            topicDtos = toTopicDtoList(problem.getListTopics());
//        }
//
//        List<TagDTO> tagDtos = null;
//        if(problem.getListTags() != null) {
//            tagDtos = toTagDtoList(problem.getListTags());
//        }
//
//        return new ProblemDTO(
//                problem.getProblemId(),
//                problem.getPublicProblemId(),
//                problem.getQuestionTitle(),
//                problem.getPageUrl(),
//                problem.getDifficultyLevel(),
//                topicDtos,
//                null, // Attempts are set later if needed
//                tagDtos
//        );
//    }
//
//    /**
//     * Converts a Topic entity list to a TopicDTO list
//     */
//    public List<TopicDTO> toTopicDtoList(List<?> topics) {
//        return topics.stream()
//                .map(t -> {
//                    var topic = (com.hackertracker.security.topic.Topic) t;
//                    return new TopicDTO(
//                            topic.getTopicId(),
//                            topic.getTopicName(),
//                            topic.getTopicRank()
//                    );
//                })
//                .toList();
//    }
//
//    /**
//     * Converts a Tag entity list to a TagDTO list
//     */
//    public List<TagDTO> toTagDtoList(List<?> tags) {
//        return tags.stream()
//                .map(t -> {
//                    var tag = (com.hackertracker.security.tag.Tag) t;
//                    return new TagDTO(tag.getTagId(), tag.getTagName());
//                })
//                .toList();
//    }
//
//    /**
//     * Converts a User entity to a UserDTO without attempts data
//     */
//    public UserDTO toUserDtoBasic(User user) {
//        if (user == null) return null;
//
//        return new UserDTO(
//                user.getUserId(),
//                user.getPublicId(),
//                user.getUsername(),
//                user.getFirstName(),
//                user.getLastName(),
//                user.getPassword(),
//                user.getEmail(),
//                user.getRole(),
//                null // Attempts are set later if needed
//        );
//    }
//
//    /**
//     * Converts a UserProblemAttempt entity to a UserProblemAttemptDTO
//     */
//    public UserProblemAttemptDTO toUserProblemAttemptDto(UserProblemAttempt attempt, ProblemDTO problemDto) {
//        if (attempt == null) return null;
//
//        UserProblemAttemptDTO dto = new UserProblemAttemptDTO();
//        dto.setAttemptId(attempt.getAttemptId());
//        dto.setDifficultyRating(attempt.getDifficultyRating());
//        dto.setStartTime(attempt.getStartTime());
//        dto.setEndTime(attempt.getEndTime());
//        dto.setProblemDto(problemDto);
//
//        return dto;
//    }
//
//    /**
//     * Converts a UserProblemPriority entity to a UserProblemPriorityDTO
//     */
//    public UserProblemPriorityDTO toUserProblemPriorityDto(
//            UserProblemPriority priority,
//            ProblemDTO problemDto,
//            UserDTO userDto) {
//
//        if (priority == null) return null;
//
//        UserProblemPriorityDTO dto = new UserProblemPriorityDTO();
//        dto.setPriorityId(priority.getPriorityId());
//        dto.setPriorityScore(priority.getPriorityScore());
//        dto.setLastCalculation(priority.getLastCalculation());
//        dto.setLastAttempted(priority.getLastAttempted());
//        dto.setProblemDto(problemDto);
//        dto.setUserDto(userDto);
//
//        return dto;
//    }
//
//    /**
//     * Converts a UserCompletionPrediction entity to a UserCompletionPredictionDTO
//     */
//    public UserCompletionPredictionDTO toUserCompletionPredictionDto(
//            UserCompletionPrediction prediction,
//            UserDTO userDto) {
//
//        if (prediction == null) return null;
//
//        return new UserCompletionPredictionDTO(
//                prediction.getPredictionId(),
//                prediction.getPredictionDate(),
//                prediction.getPredictedCompletionDate(),
//                userDto
//        );
//    }
//}