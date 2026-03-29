package com.hackertracker.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SeedDataDto {

    @JsonProperty("problems")
    private List<ProblemData> problems;

    public List<ProblemData> getProblems() {
        return problems;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProblemData {
        @JsonProperty("topic_name")
        private String topicName;

        @JsonProperty("topic_question_questionname")
        private String questionName;

        @JsonProperty("topic_question_difficulty")
        private String difficulty;

        @JsonProperty("topic_question_page")
        private String pageUrl;

        @JsonProperty("subtopic")
        private String subtopic;

        // getters
        public String getTopicName() {
            return topicName;
        }

        public String getQuestionName() {
            return questionName;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public String getPageUrl() {
            return pageUrl;
        }

        public String getSubtopic() {
            return subtopic;
        }
    }
}
