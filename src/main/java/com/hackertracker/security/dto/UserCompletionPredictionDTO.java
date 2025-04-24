//package com.hackertracker.security.dto;
//import java.util.Date;
//import java.util.Objects;
//
//
//public class UserCompletionPredictionDTO {
//    private int predictionId;
//    private Date predictionDate;
//    private Date predictedCompletionDate;
//    private UserDTO userDto;
//
//    public UserCompletionPredictionDTO(int predictionId, Date predictionDate, Date predictedCompletionDate, UserDTO userDto) {
//        this.predictionId = predictionId;
//        this.predictionDate = predictionDate;
//        this.predictedCompletionDate = predictedCompletionDate;
//        this.userDto = userDto;
//    }
//
//    public UserCompletionPredictionDTO() {
//    }
//
//    public int getPredictionId() {
//        return predictionId;
//    }
//
//    public void setPredictionId(int predictionId) {
//        this.predictionId = predictionId;
//    }
//
//    public Date getPredictionDate() {
//        return predictionDate;
//    }
//
//    public void setPredictionDate(Date predictionDate) {
//        this.predictionDate = predictionDate;
//    }
//
//    public Date getPredictedCompletionDate() {
//        return predictedCompletionDate;
//    }
//
//    public void setPredictedCompletionDate(Date predictedCompletionDate) {
//        this.predictedCompletionDate = predictedCompletionDate;
//    }
//
//    public UserDTO getUserDto() {
//        return userDto;
//    }
//
//    public void setUserDto(UserDTO userDto) {
//        this.userDto = userDto;
//    }
//
//    @Override
//    public String toString() {
//        return "UserCompletionPrediction{" +
//                "predictionId=" + predictionId +
//                ", predictionDate=" + (predictionDate != null ? predictionDate : null) +
//                ", predictedCompletionDate=" + (predictedCompletionDate != null ? predictedCompletionDate : null) +
//                ", userId=" + (userDto != null ? userDto.getUserId() : null) +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        UserCompletionPredictionDTO that = (UserCompletionPredictionDTO) o;
//        return predictionId == that.predictionId;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(predictionId);
//    }
//}