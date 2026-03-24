package com.hackertracker.security.dao;

import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserProblemCompletion;
import com.hackertracker.security.user.UserProblemPriority;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserProblemCompletionDAO {
    private final SessionFactory sessionFactory;

    public UserProblemCompletionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Find a completion by problem and user
     */
    public UserProblemCompletion findByProblemAndUser(Problem problem, User user) {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<UserProblemCompletion> cq = criteriaBuilder.createQuery(UserProblemCompletion.class);

            Root<UserProblemCompletion> root = cq.from(UserProblemCompletion.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user"), user));
            predicates.add(criteriaBuilder.equal(root.get("problem"), problem));

            cq.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

            return session.createQuery(cq).uniqueResult();

        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }


    public UserProblemCompletion save(UserProblemCompletion completion) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            if( findByProblemAndUser(completion.getProblem(), completion.getUser()) == null ) {
                session.persist(completion);
                session.getTransaction().commit();
            }
            return completion;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * Get number of problems completed
     */
    public int getNumberOfProblemsCompletedByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            // Using the typed version of createQuery, specifying the return type as Long
            Query<Long> query = session.createQuery("select count(*) from UserProblemCompletion where user.userId=:userId", Long.class);
            query.setParameter("userId", userId);
            Long count = query.uniqueResult();
            return count.intValue();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Get number of problem entities by level
     */
    public int getNumberOfProblemsByDifficultyLevelByUserId(String difficultyLevel, int userId) {
        try (Session session = sessionFactory.openSession()) {
            // Query with filter condition
            Query<Long> query = session.createQuery(
                    "select count(*) from UserProblemCompletion upc where upc.problem.difficultyLevel=:level and user.userId=:userId",
                    Long.class);
            query.setParameter("level", difficultyLevel);
            query.setParameter("userId", userId);
            Long count = query.uniqueResult();
            return count.intValue();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Find all completions
     */
    public List<UserProblemCompletion> getAllCompletedProblemsByUserId(int userId) {
        Session session = sessionFactory.openSession();

        try {
            Query<UserProblemCompletion> q = session.createQuery(
                    "from UserProblemCompletion where user.userId=:userId", UserProblemCompletion.class);

            q.setParameter("userId", userId);
            return q.list();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }
}
