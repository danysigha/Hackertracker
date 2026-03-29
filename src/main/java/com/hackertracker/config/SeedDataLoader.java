package com.hackertracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackertracker.dao.ProblemDAO;
import com.hackertracker.dao.TagDAO;
import com.hackertracker.dao.TagProblemDAO;
import com.hackertracker.dao.TopicDAO;
import com.hackertracker.dao.TopicProblemDAO;
import com.hackertracker.problem.Problem;
import com.hackertracker.problem.TagProblem;
import com.hackertracker.problem.TopicProblem;
import com.hackertracker.tag.Tag;
import com.hackertracker.topic.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class SeedDataLoader {
    private static final Logger log = LoggerFactory.getLogger(SeedDataLoader.class);

    @Value("${app.seed-data.enabled:true}")
    private boolean seedDataEnabled;

    @Value("${app.seed-data.file:classpath:data/leetcode_problems.json}")
    private String seedDataFile;

    @Bean
    public ApplicationRunner seedDatabase(
            ProblemDAO problemDAO,
            TopicDAO topicDAO,
            TagDAO tagDAO,
            TagProblemDAO tagProblemDAO,
            TopicProblemDAO topicProblemDAO,
            ResourceLoader resourceLoader,
            ObjectMapper objectMapper) {
        return args -> {
            if (!seedDataEnabled) {
                log.info("Seed data loading is disabled");
                return;
            }

            if (problemDAO.getNumberOfProblems() > 0) {
                log.info("Database already contains problems, skipping seed data load");
                return;
            }

            try {
                log.info("Starting seed data load from {}", seedDataFile);
                Resource resource = resourceLoader.getResource(seedDataFile);

                if (!resource.exists()) {
                    log.warn("Seed data file not found: {}", seedDataFile);
                    return;
                }

                SeedDataDto seedData = objectMapper.readValue(resource.getInputStream(), SeedDataDto.class);

                if (seedData.getProblems() == null || seedData.getProblems().isEmpty()) {
                    log.warn("Seed data file contained no problems");
                    return;
                }

                loadSeedData(seedData, problemDAO, topicDAO, tagDAO, tagProblemDAO, topicProblemDAO);
                log.info("Seed data loaded successfully");

            } catch (IOException e) {
                log.error("Failed to load seed data", e);
            }
        };
    }

    private void loadSeedData(
            SeedDataDto seedData,
            ProblemDAO problemDAO,
            TopicDAO topicDAO,
            TagDAO tagDAO,
            TagProblemDAO tagProblemDAO,
            TopicProblemDAO topicProblemDAO) {

        Map<String, Topic> topicMap = loadTopics(seedData, topicDAO);
        log.info("Loaded {} topics", topicMap.size());

        Map<String, Tag> tagMap = loadTags(seedData, tagDAO);
        log.info("Loaded {} tags", tagMap.size());

        List<Problem> problems = loadProblems(seedData, problemDAO, topicMap, tagMap, tagProblemDAO, topicProblemDAO);
        log.info("Loaded {} problems", problems.size());
    }

    private Map<String, Topic> loadTopics(SeedDataDto seedData, TopicDAO topicDAO) {
        Map<String, Topic> topicMap = new HashMap<>();
        Set<String> seenTopics = new HashSet<>();
        int rank = 1;

        for (SeedDataDto.ProblemData p : seedData.getProblems()) {
            String topicName = p.getTopicName();
            if (topicName != null && !topicName.isBlank() && !seenTopics.contains(topicName)) {
                Topic topic = new Topic();
                topic.setTopicName(topicName);
                topic.setTopicRank((byte) rank++);
                topicDAO.saveTopic(topic);
                topicMap.put(topicName, topic);
                seenTopics.add(topicName);
            }
        }
        return topicMap;
    }

    private Map<String, Tag> loadTags(SeedDataDto seedData, TagDAO tagDAO) {
        Map<String, Tag> tagMap = new HashMap<>();
        Set<String> seenTags = new HashSet<>();

        for (SeedDataDto.ProblemData p : seedData.getProblems()) {
            if (p.getSubtopic() == null || p.getSubtopic().isBlank())
                continue;
            for (String tagName : p.getSubtopic().split(",")) {
                tagName = tagName.trim();
                if (!tagName.isEmpty() && !seenTags.contains(tagName)) {
                    Tag tag = new Tag();
                    tag.setTagName(tagName);
                    tagDAO.saveTag(tag);
                    tagMap.put(tagName, tag);
                    seenTags.add(tagName);
                }
            }
        }
        return tagMap;
    }

    private List<Problem> loadProblems(
            SeedDataDto seedData,
            ProblemDAO problemDAO,
            Map<String, Topic> topicMap,
            Map<String, Tag> tagMap,
            TagProblemDAO tagProblemDAO,
            TopicProblemDAO topicProblemDAO) {

        List<Problem> problems = new ArrayList<>();

        for (SeedDataDto.ProblemData pd : seedData.getProblems()) {
            Problem problem = new Problem();
            problem.setPublicProblemId(generateUUID());
            problem.setQuestionTitle(pd.getQuestionName());
            problem.setDifficultyLevel(pd.getDifficulty());
            problem.setPageUrl(pd.getPageUrl() != null ? pd.getPageUrl() : "");

            problemDAO.saveProblem(problem);
            problems.add(problem);

            // Associate tags from comma-separated subtopic field
            if (pd.getSubtopic() != null && !pd.getSubtopic().isBlank()) {
                for (String tagName : pd.getSubtopic().split(",")) {
                    tagName = tagName.trim();
                    Tag tag = tagMap.get(tagName);
                    if (tag != null) {
                        tagProblemDAO.saveTagProblem(new TagProblem(problem, tag));
                    }
                }
            }

            // Associate topic
            if (pd.getTopicName() != null) {
                Topic topic = topicMap.get(pd.getTopicName());
                if (topic != null) {
                    topicProblemDAO.saveTopicProblem(new TopicProblem(problem, topic));
                }
            }
        }
        return problems;
    }

    private String generateUUID() {
        return java.util.UUID.randomUUID().toString();
    }
}