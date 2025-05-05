package com.hackertracker.security.dao;

import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.user.User;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Data Access Object for User entity
 * Refactored to remove circular dependency
 */
@Repository
public class UserDAO {

    private final SessionFactory sessionFactory;

    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Get all User entities
     */
    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    /**
     * Save a new User entity
     */
    public void saveUser(User user) {
//        System.out.println("The user we want to save \n" + user);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Get a User entity by ID
     */
    public User getUserById(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> q = session.createQuery("from User where userId=:userId", User.class);
            q.setParameter("userId", userId);
            return q.uniqueResult();
        }
    }

//    /**
//     * Get a User entity by ID
//     */
//    @Transactional(readOnly = true)
//    public User getUserByIdWithCollections(int userId) {
//        Session session = sessionFactory.openSession();
//        User user = session.get(User.class, userId);
//        Hibernate.initialize(user.getListAttempts());
//        Hibernate.initialize(user.getUserSchedule());
//        return user;
//    }

    /**
     * Get a User entity by ID with collections initialized
     */
    /**
     * Get a User entity by ID with collections initialized
     */
    public User getUserByIdWithCollections(int userId) {
        Session session = sessionFactory.openSession();
        try {
            User user = session.get(User.class, userId);

            if (user != null) {
                // Force initialization of collections while session is still open
                Hibernate.initialize(user.getListAttempts());
                Hibernate.initialize(user.getListProblemPriorities());
                Hibernate.initialize(user.getUserSchedule());
                Hibernate.initialize(user.getTopicRanks());
            }

            return user;
        } finally {
            session.close(); // Make sure to close the session
        }
    }
//    @Transactional(readOnly = true)
//    public User getUserByIdWithCollections(int userId) {
//        Session session = sessionFactory.getCurrentSession();
//        User user = session.get(User.class, userId);
//
//        if (user != null) {
//            Hibernate.initialize(user.getListAttempts());
//            Hibernate.initialize(user.getListProblemPriorities());
//            Hibernate.initialize(user.getUserSchedule());
//        }
//
//        return user;
//    }

    /**
     * Get a User entity by username with schedule
     */
    @Transactional(readOnly = true)
    public User getUserByUsernameWithSchedule(String username) {
        // Use the current transaction's session rather than opening a new one
        Session session = sessionFactory.openSession();

        try {
            // Query directly in this method instead of calling getUserByUserName
            Query<User> q = session.createQuery("from User where userName=:userName", User.class);
            q.setParameter("userName", username);
            User user = q.uniqueResult();

            // Initialize the collections within the same session
            if (user != null && user.getUserSchedule() != null) {
                Hibernate.initialize(user.getUserSchedule().getSchedule());
            }

            return user;
        }
        finally {
            session.close();
        }
    }

    /**
     * Get a User entity by username with schedule
     */
    @Transactional(readOnly = true)
    public User getUserByIdWithTopics(int userId) {
        // Use the current transaction's session rather than opening a new one
        Session session = sessionFactory.openSession();

        try {
            // Query directly in this method instead of calling getUserByUserName
            Query<User> q = session.createQuery("from User where userId=:userId", User.class);
            q.setParameter("userId", userId);
            User user = q.uniqueResult();

            // Initialize the collections within the same session
            if (user != null && user.getTopicRanks() != null) {
                Hibernate.initialize(user.getTopicRanks().getTopics());
            }

            return user;
        }
        finally {
            session.close();
        }
    }

    /**
     * Get a User entity by public ID
     */
    public User getUserByPublicId(String publicId) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> q = session.createQuery("from User where publicId=:publicId", User.class);
            q.setParameter("publicId", publicId);
            return q.uniqueResult();
        }
    }

    /**
     * Get a User entity by username
     */
    public User getUserByUserName(String userName) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> q = session.createQuery("from User where userName=:userName", User.class);
            q.setParameter("userName", userName);
            return q.uniqueResult();
        }
    }

    /**
     * Get a User entity by email
     */
    public User getUserByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> q = session.createQuery("from User where email=:email", User.class);
            q.setParameter("email", email);
            return q.uniqueResult();
        }
    }

    /**
     * Update an existing User entity
     */
    public void updateUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.merge(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Delete a User entity by ID
     */
    public void deleteUser(int id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.remove(getUserById(id));
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Attach (merge) a detached User entity to a session
     */
    public User attachUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                User mergedUser = session.merge(user);
                tx.commit();
                return mergedUser;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}