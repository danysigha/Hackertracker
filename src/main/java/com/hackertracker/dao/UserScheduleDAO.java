package com.hackertracker.dao;

import com.hackertracker.user.User;
import com.hackertracker.user.UserSchedule;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UserScheduleDAO {

    private final SessionFactory sessionFactory;

    public UserScheduleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Get a user's Schedule entity
     */
    /**
     * Get a user's Schedule entity
     */
    public UserSchedule getScheduleByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            // Use join fetch to eagerly load the schedule collection
            Query<UserSchedule> q = session.createQuery(
                    "from UserSchedule us join fetch us.schedule where us.user = :user",
                    UserSchedule.class);
            q.setParameter("user", user);
            return q.uniqueResult();
        }
    }

    /**
     * Update an existing User's schedule entity
     */
    public void updateUserSchedule(UserSchedule userSchedule) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.merge(userSchedule);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
