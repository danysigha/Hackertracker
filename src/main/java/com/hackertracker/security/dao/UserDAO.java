package com.hackertracker.security.dao;

import com.hackertracker.security.dto.UserDTO;
import com.hackertracker.security.dto.UserProblemService;
import com.hackertracker.security.user.User;
import com.hackertracker.security.dto.DTOMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data Access Object for User entity
 * Refactored to remove circular dependency
 */
@Repository
public class UserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserProblemService userProblemService;

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
        System.out.println("The user we want to save \n" + user);
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
    public User getUserById(Integer userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> q = session.createQuery("from User where userId=:userId", User.class);
            q.setParameter("userId", userId);
            return q.uniqueResult();
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

    public UserDTO getUserDtoByUserName(String userName) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> q = session.createQuery("from User where userName=:userName", User.class);
            q.setParameter("userName", userName);
            User user = q.uniqueResult();
            UserDTO userDto = new UserDTO(user.getUserId(), user.getPublicId(), user.getUserName(), user.getFirstName(),
                    user.getLastName(), user.getPassword(), user.getEmail(), user.getRole(), userProblemService.getUserAttempts(user));
            System.out.println("within session " + userDto);
            return userDto;
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