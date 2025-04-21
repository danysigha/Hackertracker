package com.hackertracker.security.dao;

import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.dto.DTOMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
}