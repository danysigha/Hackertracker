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
 * Data Access Object for Problem entity
 * Refactored to remove circular dependency
 *
 * @author danysigha
 */
@Repository
public class ProblemDAO {

    private final SessionFactory sessionFactory;

    public ProblemDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Save a new Problem entity
     */
    public void saveProblem(Problem problem) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(problem);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Update an existing Problem entity
     */
    public void updateProblem(Problem problem) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.merge(problem);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Get a Problem entity by its ID
     */
    public Problem getProblemById(int problemId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Problem> q = session.createQuery("from Problem where problemId=:problemId", Problem.class);
            q.setParameter("problemId", problemId);
            return q.uniqueResult();
        }
    }

    /**
     * Get a Problem entity by its ID
     */
    @Transactional(readOnly = true)
    public Problem getProblemByIdWithCollections(int problemId) {
        Session session = sessionFactory.openSession();
        Problem problem = session.get(Problem.class, problemId);
        Hibernate.initialize(problem.getProblemTags());
        Hibernate.initialize(problem.getProblemTopics());
        return problem;
    }

    /**
     * Get a Problem entity by its public ID
     */
    public Problem getProblemByPublicId(String publicProblemId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Problem> q = session.createQuery("from Problem where publicProblemId=:publicProblemId", Problem.class);
            q.setParameter("publicProblemId", publicProblemId);
            return q.uniqueResult();
        }
    }

    /**
     * Get all Problem entities
     */
    public List<Problem> getAllProblems() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Problem", Problem.class).list();
        }
    }

    /**
     * Get number of problem entities
     */
    public int getNumberOfProblems() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(*) from Problem", Long.class);
            Long count = query.uniqueResult();
            return count.intValue();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Get number of problem entities by level
     */
    public int getNumberOfProblemsByDifficultyLevel(String difficultyLevel) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(*) from Problem where difficultyLevel=:difficultyLevel", Long.class);
            query.setParameter("difficultyLevel", difficultyLevel);
            Long count = query.uniqueResult();
            return count.intValue();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Get Problem entities by difficulty level
     */
    public List<Problem> getProblemsByDifficultyLevel(String difficultyLevel) {
        try (Session session = sessionFactory.openSession()) {
            Query<Problem> q = session.createQuery("from Problem where difficultyLevel=:difficultyLevel", Problem.class);
            q.setParameter("difficultyLevel", difficultyLevel);
            return q.list();
        }
    }

    /**
     * Search for Problem entities by title (partial match)
     */
    public List<Problem> searchProblemsByQuestionTitle(String questionTitle) {
        try (Session session = sessionFactory.openSession()) {
            Query<Problem> q = session.createQuery("from Problem where questionTitle LIKE :questionTitle", Problem.class);
            q.setParameter("questionTitle", "%" + questionTitle + "%");
            return q.list();
        }
    }

    /**
     * Attach (merge) a detached Problem entity to a session
     */
    public Problem attachProblem(Problem problem) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Problem mergedProblem = session.merge(problem);
                tx.commit();
                return mergedProblem;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Check if problems of a specific difficulty are available
     */
//    public boolean hasProblemsByDifficulty(int userId, String difficulty) {
//        try (Session session = sessionFactory.openSession()){
//            Query<Long> q = session.createQuery("SELECT COUNT(p) FROM Problem p " +
//                    "WHERE p.difficultyLevel = :difficulty " +
//                    "AND p.id NOT IN (SELECT cp.problem.problemId FROM UserProblemCompletion cp WHERE cp.user.userId = :userId)", Long.class);
//
//            q.setParameter("userId", userId);
//            q.setParameter("difficulty", difficulty);
//
//            Long count = q.getSingleResult();
//
//            return count > 0;
//
//        } catch (Exception e) {
//            throw e;
//        }
//    }
}