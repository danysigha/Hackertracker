package com.hackertracker.security.dao;

import com.hackertracker.security.dto.ProblemDTO;
import com.hackertracker.security.dto.UserProblemService;
import com.hackertracker.security.topic.Topic;
import com.hackertracker.security.dto.TopicDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author danysigha
 */
@Component
@Repository
public class TopicDAO {

    @Autowired
    SessionFactory sessionFactory;
    @Autowired
    UserProblemService userProblemService;

    public List<TopicDTO> getAllTopics(){
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Topic", Topic.class).list().stream().map(
                    (t) -> new TopicDTO(t.getTopicId(), t.getTopicName(), t.getTopicRank())
            ).toList();
        } catch (Exception e) {
            throw e;
        }
    }

    public Topic getTopicById(byte topicId) {
        Session session = sessionFactory.openSession();
        Query<Topic> q = session.createQuery("from Tag where topicId=:topicId", Topic.class);
        q.setParameter("topicId", topicId);
        return q.uniqueResult();
    }

    public List<TopicDTO> getTopicByName(String topicName) {
        Session session = sessionFactory.openSession();
//        Query<Tag> q = session.createQuery("from Tag where tagName=:tagName", Tag.class);
        Query<Topic> q = session.createQuery("from Topic where topicName LIKE :topicName", Topic.class);
        q.setParameter("topicName", "%" + topicName + "%");
//        q.setParameter("tagName", tagName);
//        return q.uniqueResult();
        return q.list().stream().map(
                (t) -> new TopicDTO(t.getTopicId(), t.getTopicName(), t.getTopicRank())
        ).toList();
    }

    public List<ProblemDTO> getTopicProblems(Topic topic) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            List<ProblemDTO> problems = topic.getListProblems().stream().map(
                    (problem) -> {
                        ProblemDTO problemDto = new ProblemDTO();
                        problemDto.setProblemId(problem.getProblemId());
                        problemDto.setPublicProblemId(problem.getPublicProblemId());
                        problemDto.setQuestionTitle(problem.getQuestionTitle());
                        problemDto.setDifficultyLevel(problem.getDifficultyLevel());
                        problemDto.setPageUrl(problem.getPageUrl());
                        problemDto.setTopics(userProblemService.getProblemTopics(problem));
                        problemDto.setAttempts(userProblemService.getProblemAttempts(problem, problemDto));
                        return problemDto;
                    }
            ).toList();
            tx.commit();
            return problems;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}