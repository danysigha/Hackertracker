package com.hackertracker.dao;

import com.hackertracker.problem.Problem;
import com.hackertracker.user.User;
import com.hackertracker.user.UserProblemCompletion;
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
    
    private static final String USER_ID_PARAM = "userId";
    
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
            Query<Long> query = session.createQuery("select count(*) from UserProblemCompletion where user.userId=:" + USER_ID_PARAM, Long.class);
            query.setParameter(USER_ID_PARAM, userId);
            Long count = query.uniqueResult();
            return count.intValue();
        }
    }


    /**
     * Get number of problem entities by level
     */
    public int getNumberOfProblemsByDifficultyLevelByUserId(String difficultyLevel, int userId) {
        try (Session session = sessionFactory.openSession()) {
            // Query with filter condition
            Query<Long> query = session.createQuery(
                    "select count(*) from UserProblemCompletion upc where upc.problem.difficultyLevel=:level and user.userId=:" + USER_ID_PARAM,
                    Long.class);
            query.setParameter("level", difficultyLevel);
            query.setParameter(USER_ID_PARAM, userId);
            Long count = query.uniqueResult();
            return count.intValue();
        }
    }


    /**
     * Find all completions
     */
    public List<UserProblemCompletion> getAllCompletedProblemsByUserId(int userId) {
        Session session = sessionFactory.openSession();

        try {
            Query<UserProblemCompletion> q = session.createQuery(
                    "from UserProblemCompletion where user.userId=:" + USER_ID_PARAM, UserProblemCompletion.class);

            q.setParameter(USER_ID_PARAM, userId);
            return q.list();
        } finally {
            session.close();
        }
    }
}
