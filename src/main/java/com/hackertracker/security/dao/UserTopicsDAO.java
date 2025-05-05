package com.hackertracker.security.dao;

import com.hackertracker.security.user.UserTopics;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
     * Save a new User entity
     */
    public void saveUserTopics(UserTopics userTopics) {
//        System.out.println("The user we want to save \n" + user);
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
