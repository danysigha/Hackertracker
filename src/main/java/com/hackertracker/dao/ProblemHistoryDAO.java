package com.hackertracker.dao;

import com.hackertracker.problem.ProblemHistory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProblemHistoryDAO {

    private final SessionFactory sessionFactory;

    public ProblemHistoryDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Save a new ProblemHistory entity
     */
    public void saveProblemHistory(ProblemHistory ph) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(ph);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Get a ProblemHistory entity by its user ID
     */
    public List<ProblemHistory> getProblemHistoryByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<ProblemHistory> q = session.createQuery("from ProblemHistory where user.userId=:userId", ProblemHistory.class);
            q.setParameter("userId", userId);
            return q.list();
        }
    }

    /**
     * Load recent problem difficulties from database
     */
    public List<String> loadRecentDifficulties(int userId) {
        try (Session session = sessionFactory.openSession()){
            Query<String> q = session.createQuery("SELECT ph.problem.difficultyLevel FROM ProblemHistory ph " +
                    "WHERE ph.user.userId = :userId " +
                    "ORDER BY ph.viewTimestamp DESC", String.class).setMaxResults(10);

            q.setParameter("userId", userId);

            List<String> difficulties = q.getResultList();

            // Convert to lowercase for consistency
            difficulties.replaceAll(String::toLowerCase);

            return difficulties;

        }
    }
}
