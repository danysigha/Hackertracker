package com.hackertracker.security.dao;

import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.problem.ProblemHistory;
import com.hackertracker.security.user.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
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
//        System.out.println("The user we want to save \n" + user);
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
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Load recent problem difficulties from database
     */
    public LinkedList<String> loadRecentDifficulties(int userId) {
        try (Session session = sessionFactory.openSession()){
            Query<String> q = session.createQuery("SELECT ph.problem.difficultyLevel FROM ProblemHistory ph " +
                    "WHERE ph.user.userId = :userId " +
                    "ORDER BY ph.viewTimestamp DESC", String.class).setMaxResults(10);

            q.setParameter("userId", userId);

            List<String> difficulties = q.getResultList();

            // Convert to lowercase for consistency
            difficulties.replaceAll(String::toLowerCase);

            return new LinkedList<>(difficulties);

        } catch (Exception e) {
            throw e;
        }
    }
}
