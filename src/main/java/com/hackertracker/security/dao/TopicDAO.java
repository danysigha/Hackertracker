package com.hackertracker.security.dao;

import com.hackertracker.security.dto.TopicDTO;
import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.topic.Topic;
import com.hackertracker.security.user.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
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

    private final SessionFactory sessionFactory;

    public TopicDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Topic> getAllTopics(){
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Topic order by topicRank", Topic.class).list();
        } catch (Exception e) {
            throw e;
        }
    }

    public List<TopicDTO> getAllTopicsDtos(){
        try (Session session = sessionFactory.openSession()) {
            List<Topic> topics = session.createQuery("FROM Topic order by topicRank", Topic.class).list();
            return topics.stream().map(TopicDTO::fromEntity).toList();
        } catch (Exception e) {
            throw e;
        }
    }

    public Topic getTopicById(byte topicId) {
        Session session = sessionFactory.openSession();
        Query<Topic> q = session.createQuery("from Topic where topicId=:topicId", Topic.class);
        q.setParameter("topicId", topicId);
        return q.uniqueResult();
    }

    public List<Topic> getTopicByName(String topicName) {
        Session session = sessionFactory.openSession();
        Query<Topic> q = session.createQuery("from Topic where topicName LIKE :topicName", Topic.class);
        q.setParameter("topicName", "%" + topicName + "%");
        return q.list();
    }

    public List<Problem> getTopicProblems(Topic topic) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            List<Problem> topicProblems = topic.getListProblems();
            tx.commit();
            return topicProblems;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }


    /**
     * Update an existing topic entity
     */
    public void updateTopic(Topic topic) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.merge(topic);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}