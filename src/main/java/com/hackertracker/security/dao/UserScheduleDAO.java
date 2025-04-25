package com.hackertracker.security.dao;

import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserSchedule;
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
    public UserSchedule getScheduleByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<UserSchedule> q = session.createQuery("from UserSchedule where user=:user", UserSchedule.class);
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
