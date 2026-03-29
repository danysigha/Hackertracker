package com.hackertracker.dao;

import com.hackertracker.user.UserTopics;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data Access Object for UserTopics entity
 */
@Repository
public class UserTopicsDAO {

    private final SessionFactory sessionFactory;

    public UserTopicsDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    /**
     * Get all User entities
     */
    public List<UserTopics> getAllUserTopics() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM UserTopics", UserTopics.class).list();
        }
    }

    /**
     * Get User topics entities by user id
     */
    public UserTopics getUserTopicsByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<UserTopics> q = session.createQuery("FROM UserTopics where user.userId =: userId", UserTopics.class);
            q.setParameter("userId", userId);
            UserTopics userTopics = q.uniqueResult();
            Hibernate.initialize(userTopics.getTopics());
            return userTopics;
        }
    }

    /**
     * Save a new User entity
     */
    public void saveUserTopics(UserTopics userTopics) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(userTopics);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
