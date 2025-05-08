package com.hackertracker.security.problem;

import com.hackertracker.security.dto.ProblemWithAttemptDTO;
import com.hackertracker.security.dto.UserProblemAttemptDTO;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;

import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.mapper.orm.Search;
import org.springframework.stereotype.Service;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProblemSearchService {

    private final SessionFactory sessionFactory;

    public ProblemSearchService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<ProblemWithAttemptDTO> searchProblems(
            String title, List<String> tags, List<String> topics,
            List<String> difficulties, Boolean completed, int userId, int limit
    ) {
        // Get a Hibernate Session
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            SearchSession searchSession = Search.session(session);
            SearchPredicateFactory f = searchSession.scope(Problem.class).predicate();

            // Create a boolean predicate
            var boolPredicate = f.bool();


            if (title != null && !title.isBlank()) {
                boolPredicate.must(f.match()
                        .field("questionTitle")
                        .matching(title)
                        .fuzzy(2));
            }

            if (tags != null && !tags.isEmpty()) {
                var tagsBool = f.bool();
                for (String tag : tags) {
                    tagsBool.should(f.match()
                            .field("problemTags.tag.tagName")
                            .matching(tag));
                }
                boolPredicate.must(tagsBool);
            }

            if (topics != null && !topics.isEmpty()) {
                var topicsBool = f.bool();
                for (String topic : topics) {
                    topicsBool.should(f.match()
                            .field("problemTopics.topic.topicName")
                            .matching(topic));
                }
                boolPredicate.must(topicsBool);
            }

            if (difficulties != null && !difficulties.isEmpty()) {
                var difficultiesBool = f.bool();
                for (String difficulty : difficulties) {
                    difficultiesBool.should(f.match()
                            .field("difficultyLevel")
                            .matching(difficulty));
                }
                boolPredicate.must(difficultiesBool);
            }

            // If no conditions were provided, match all
            if ((title == null || title.isBlank()) &&
                    (tags == null || tags.isEmpty()) &&
                    (topics == null || topics.isEmpty()) &&
                    (difficulties == null || difficulties.isEmpty())) {
                boolPredicate.must(f.matchAll());
            }

            // If we need to filter by completion status and have a userId
            if (completed != null && userId != -1) {
                // Rather than using search, it's more efficient to use a JPQL query
                // to get the list of completed problem IDs
                List<Integer> completedProblemIds = session.createQuery(
                                "SELECT pc.problem.problemId FROM UserProblemCompletion pc " +
                                        "WHERE pc.user.userId = :userId", Integer.class)
                        .setParameter("userId", userId)
                        .list();

                if (completed) {
                    // Include only completed problems
                    if (completedProblemIds.isEmpty()) {
                        // If no completed problems, return empty result
                        return Collections.emptyList();
                    }
                    boolPredicate.must(f.id().matchingAny(
                            completedProblemIds.stream().map(String::valueOf).collect(Collectors.toList())
                    ));
                } else {
                    // Exclude completed problems
                    if (!completedProblemIds.isEmpty()) {
                        boolPredicate.mustNot(f.id().matchingAny(
                                completedProblemIds.stream().map(String::valueOf).collect(Collectors.toList())
                        ));
                    }
                }
            }

            SearchResult<Problem> result = searchSession.search(Problem.class)
                    .where(boolPredicate.toPredicate())
                    .sort(p -> p.field("difficultyLevel")).fetch(limit);

            long totalHitCount = result.total().hitCount();
            List<Problem> problems = result.hits();

            // Get your results
            List<ProblemWithAttemptDTO> dtoList = problems.stream().map( (p) -> {
                List<UserProblemAttemptDTO> attempts = p.getListAttempts().stream()
                        .map(UserProblemAttemptDTO::fromEntity).toList();

                ProblemWithAttemptDTO dto = createProblemWithAttemptDTO(p, attempts);

                // Add completion status if userId is provided
                if (userId != -1) {
                    dto.setCompleted(p.isCompletedByUser(userId));
                }

                return dto;
            }).collect(Collectors.toList());

            tx.commit();
            return dtoList;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error searching problems", e);
        } finally {
            session.close();
        }
    }

//    @Transactional(readOnly = true)
//    public List<ProblemWithAttemptDTO> searchProblems(
//            String title, List<String> tags, List<String> topics,
//            List<String> difficulties, Boolean completed, int userId, int limit
//    ) {
//        // Get a Hibernate Session
//        Session session = sessionFactory.getCurrentSession();
//
//        SearchSession searchSession = Search.session(session);
//
//        SearchPredicateFactory f = searchSession.scope(Problem.class).predicate();
//
//        // Create a boolean predicate
//        var boolPredicate = f.bool();
//
////        if (title != null && !title.isBlank()) {
////            boolPredicate.must(f.match()
////                    .field("questionTitle")
////                    .matching(title));
////        }
//
//        if (title != null && !title.isBlank()) {
//            boolPredicate.must(f.match()
//                    .field("questionTitle")
//                    .matching(title)
//                    .fuzzy(2));
//        }
//
//        if (tags != null && !tags.isEmpty()) {
//            var tagsBool = f.bool();
//            for (String tag : tags) {
//                tagsBool.should(f.match()
//                        .field("problemTags.tag.tagName")
//                        .matching(tag));
//            }
//            boolPredicate.must(tagsBool);
//        }
//
//        if (topics != null && !topics.isEmpty()) {
//            var topicsBool = f.bool();
//            for (String topic : topics) {
//                topicsBool.should(f.match()
//                        .field("problemTopics.topic.topicName")
//                        .matching(topic));
//            }
//            boolPredicate.must(topicsBool);
//        }
//
//        if (difficulties != null && !difficulties.isEmpty()) {
//            var difficultiesBool = f.bool();
//            for (String difficulty : difficulties) {
//                difficultiesBool.should(f.match()
//                        .field("difficultyLevel")
//                        .matching(difficulty));
//            }
//            boolPredicate.must(difficultiesBool);
//        }
//
//        // If no conditions were provided, match all
//        if ((title == null || title.isBlank()) &&
//                (tags == null || tags.isEmpty()) &&
//                (topics == null || topics.isEmpty()) &&
//                (difficulties == null || difficulties.isEmpty())) {
//            boolPredicate.must(f.matchAll());
//        }
//
//        // If we need to filter by completion status and have a userId
//        if (completed != null && userId != -1) {
//            // Rather than using search, it's more efficient to use a JPQL query
//            // to get the list of completed problem IDs
//            List<Integer> completedProblemIds = session.createQuery(
//                            "SELECT pc.problem.problemId FROM UserProblemCompletion pc " +
//                                    "WHERE pc.user.userId = :userId", Integer.class)
//                    .setParameter("userId", userId)
//                    .list();
//
//            if (completed) {
//                // Include only completed problems
//                if (completedProblemIds.isEmpty()) {
//                    // If no completed problems, return empty result
//                    return Collections.emptyList();
//                }
//                boolPredicate.must(f.id().matchingAny(
//                        completedProblemIds.stream().map(String::valueOf).collect(Collectors.toList())
//                ));
//            } else {
//                // Exclude completed problems
//                if (!completedProblemIds.isEmpty()) {
//                    boolPredicate.mustNot(f.id().matchingAny(
//                            completedProblemIds.stream().map(String::valueOf).collect(Collectors.toList())
//                    ));
//                }
//            }
//        }
//
//        SearchResult<Problem> result = searchSession.search(Problem.class)
//                .where(boolPredicate.toPredicate())
//                .sort(p -> p.field("difficultyLevel")).fetch(limit);
//
//        long totalHitCount = result.total().hitCount();
//
//        List<Problem> problems = result.hits();
//
//        return problems.stream().map( (p) -> {
//
//            List<UserProblemAttemptDTO> attempts = p.getListAttempts().stream()
//                    .map(UserProblemAttemptDTO::fromEntity).toList();
//
//            ProblemWithAttemptDTO dto = createProblemWithAttemptDTO(p, attempts);
//
//            // Add completion status if userId is provided
//            if (userId != -1) {
//                dto.setCompleted(p.isCompletedByUser(userId));
//            }
//
//            return dto;
//
//        }).collect(Collectors.toList());
//    }

    private ProblemWithAttemptDTO createProblemWithAttemptDTO(Problem problem, List<UserProblemAttemptDTO> attempts) {
        UserProblemAttemptDTO latestAttempt = attempts.stream()
                .sorted(Comparator.comparing(UserProblemAttemptDTO::getEndTime).reversed())
                .findFirst().orElse(null);

        return new ProblemWithAttemptDTO(problem, latestAttempt);
    }
}
