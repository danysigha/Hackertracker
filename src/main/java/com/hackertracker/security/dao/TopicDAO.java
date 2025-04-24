package com.hackertracker.security.dao;

import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.topic.Topic;
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

    private final SessionFactory sessionFactory;

    public TopicDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Topic> getAllTopics(){
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Topic", Topic.class).list();
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

    public List<Topic> getTopicByName(String topicName) {
        Session session = sessionFactory.openSession();
//        Query<Tag> q = session.createQuery("from Tag where tagName=:tagName", Tag.class);
        Query<Topic> q = session.createQuery("from Topic where topicName LIKE :topicName", Topic.class);
        q.setParameter("topicName", "%" + topicName + "%");
//        q.setParameter("tagName", tagName);
//        return q.uniqueResult();
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
}