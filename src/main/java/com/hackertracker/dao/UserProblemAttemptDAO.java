package com.hackertracker.dao;


import com.hackertracker.problem.Problem;
import com.hackertracker.user.User;
import com.hackertracker.user.UserProblemAttempt;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for UserProblemAttempt entity
 * Refactored to remove circular dependency
 */
@Repository
public class UserProblemAttemptDAO {

    private final SessionFactory sessionFactory;

    public UserProblemAttemptDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Save a new Attempt entity
     */
    public void saveAttempt(UserProblemAttempt newAttempt) {

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                List<UserProblemAttempt> attempts = findByProblemAndUser(newAttempt.getProblem(), newAttempt.getUser());

                for(UserProblemAttempt attempt : attempts) {
                    if( attempt.equals(newAttempt) ) {
                        tx.rollback();
                        return;
                    }
                }

                session.persist(newAttempt);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }


    /**
     * Find an attempt by problem and user
     */
    public List<UserProblemAttempt> findByProblemAndUser(Problem problem, User user) {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<UserProblemAttempt> cq = criteriaBuilder.createQuery(UserProblemAttempt.class);

            Root<UserProblemAttempt> root = cq.from(UserProblemAttempt.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user"), user));
            predicates.add(criteriaBuilder.equal(root.get("problem"), problem));

            cq.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

            return session.createQuery(cq).list();

        } finally {
            session.close();
        }
    }


    /**
     * Get the latest attempt for a problem
     */
    public UserProblemAttempt getLatestAttempt(Problem problem, User user) {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<UserProblemAttempt> cq = criteriaBuilder.createQuery(UserProblemAttempt.class);
            Root<UserProblemAttempt> root = cq.from(UserProblemAttempt.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user"), user));
            predicates.add(criteriaBuilder.equal(root.get("problem"), problem));

            cq.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
            cq.orderBy(criteriaBuilder.desc(root.get("endTime")));

            List<UserProblemAttempt> results = session.createQuery(cq)
                    .setMaxResults(1)
                    .getResultList();

            return results.isEmpty() ? null : results.get(0);
        } finally {
            session.close();
        }
    }


    /**
     * Get number of attempts
     */
    public int getNumberOfAttemptsByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(*) from UserProblemAttempt where user.userId=:userId", Long.class);
            query.setParameter("userId", userId);
            Long count = query.uniqueResult();
            return count.intValue();
        }
    }

    /**
     * Get number of attempts
     */
    public int getNumberOfAttempts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(*) from UserProblemAttempt", Long.class);
            Long count = query.uniqueResult();
            return count.intValue();

        }
    }
}
