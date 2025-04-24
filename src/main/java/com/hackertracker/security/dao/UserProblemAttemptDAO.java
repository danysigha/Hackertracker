package com.hackertracker.security.dao;


import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserProblemAttempt;
import com.hackertracker.security.user.UserProblemPriority;
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
import java.util.Date;
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
                session.persist(newAttempt);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
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
}
