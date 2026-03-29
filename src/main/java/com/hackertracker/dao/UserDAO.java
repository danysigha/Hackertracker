package com.hackertracker.dao;

import com.hackertracker.user.User;
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

    private static final String USER_ID_PARAM = "userId";
    private static final String USER_IDS_PARAM = "userIds";
    
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

    public List<Integer> getAllUserIds() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("SELECT u.userId FROM User u", Integer.class).list();
        } finally {
            session.close();
        }
    }

    /**
     * Save a new User entity
     */
    public void saveUser(User user) {
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
            Query<User> q = session.createQuery("from User where userId=:" + USER_ID_PARAM, User.class);
            q.setParameter(USER_ID_PARAM, userId);
            return q.uniqueResult();
        }
    }

    /**
     * Get a User entity by ID with collections initialized
     */
    public User getUserByIdWithCollections(int userId) {
        Session session = sessionFactory.openSession();
        try {
            // Use multiple queries with different join fetches to avoid Cartesian products
            // First, get the user with topic ranks
            User user = session.createQuery(
                            "FROM User u " +
                                    "LEFT JOIN FETCH u.topicRanks tr " +
                                    "LEFT JOIN FETCH tr.topics " +
                                    "WHERE u.userId = :" + USER_ID_PARAM,
                            User.class)
                    .setParameter(USER_ID_PARAM, userId)
                    .uniqueResult();

            if (user != null) {
                // Then load attempts in a separate query
                // This gives us the same user object but with attempts loaded
                user = session.createQuery(
                                "FROM User u " +
                                        "LEFT JOIN FETCH u.problemAttempts a " +
                                        "LEFT JOIN FETCH a.problem " +
                                        "WHERE u.userId = :" + USER_ID_PARAM,
                                User.class)
                        .setParameter(USER_ID_PARAM, userId)
                        .uniqueResult();

                // Load problem priorities
                user = session.createQuery(
                                "FROM User u " +
                                        "LEFT JOIN FETCH u.problemPriorities " +
                                        "WHERE u.userId = :" + USER_ID_PARAM,
                                User.class)
                        .setParameter(USER_ID_PARAM, userId)
                        .uniqueResult();

                // Load user schedule
                user = session.createQuery(
                                "FROM User u " +
                                        "LEFT JOIN FETCH u.userSchedule " +
                                        "WHERE u.userId = :" + USER_ID_PARAM,
                                User.class)
                        .setParameter(USER_ID_PARAM, userId)
                        .uniqueResult();
            }

            return user;
        } finally {
            session.close();
        }
    }



    public List<User> getUsersWithCollectionsByIds(List<Integer> userIds) {
        Session session = sessionFactory.openSession();
        try {

            List<User> users = session.createQuery(
                            "FROM User u " +
                                    "LEFT JOIN FETCH u.topicRanks tr " +
                                    "LEFT JOIN FETCH tr.topics " +
                                    "WHERE u.userId IN (:" + USER_IDS_PARAM + ")",
                            User.class)
                    .setParameter(USER_IDS_PARAM, userIds)
                    .getResultList();

            if (!users.isEmpty()) {
                // Then load attempts in a separate query
                // This gives us the same user object but with attempts loaded
                users = session.createQuery(
                                "FROM User u " +
                                        "LEFT JOIN FETCH u.problemAttempts a " +
                                        "LEFT JOIN FETCH a.problem " +
                                        "WHERE u.userId IN (:" + USER_IDS_PARAM + ")",
                                User.class)
                        .setParameter(USER_IDS_PARAM, userIds)
                        .getResultList();

                // Load problem priorities
                users = session.createQuery(
                                "FROM User u " +
                                        "LEFT JOIN FETCH u.problemPriorities " +
                                        "WHERE u.userId IN (:" + USER_IDS_PARAM + ")",
                                User.class)
                        .setParameter(USER_IDS_PARAM, userIds)
                        .getResultList();

                // Load user schedule
                users = session.createQuery(
                                "FROM User u " +
                                        "LEFT JOIN FETCH u.userSchedule " +
                                        "WHERE u.userId IN (:" + USER_IDS_PARAM + ")",
                                User.class)
                        .setParameter(USER_IDS_PARAM, userIds)
                        .getResultList();
            }

            return users;

        } finally {
            session.close();
        }
    }

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
            Query<User> q = session.createQuery("from User where userId=:" + USER_ID_PARAM, User.class);
            q.setParameter(USER_ID_PARAM, userId);
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