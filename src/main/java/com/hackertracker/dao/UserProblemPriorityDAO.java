package com.hackertracker.dao;

import com.hackertracker.problem.Problem;
import com.hackertracker.user.User;
import com.hackertracker.user.UserProblemPriority;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for user problem priorities
 */
@Repository
public class UserProblemPriorityDAO {

    private static final String USER_ID_PARAM = "userId";
    private static final String PRIORITY_ID_PARAM = "priorityId";

    private final SessionFactory sessionFactory;

    public UserProblemPriorityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Find a priority by problem and user
     */
    public UserProblemPriority findByProblemAndUser(Problem problem, User user) {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<UserProblemPriority> cq = criteriaBuilder.createQuery(UserProblemPriority.class);

            Root<UserProblemPriority> root = cq.from(UserProblemPriority.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user"), user));
            predicates.add(criteriaBuilder.equal(root.get("problem"), problem));

            cq.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

            return session.createQuery(cq).uniqueResult();

        } finally {
            session.close();
        }
    }

    public void normalizeScoresByUserSql(int userId, double minScore, double range) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                // Direct SQL update is much faster
                session.createNativeQuery(
                        "UPDATE user_problem_priority " +
                                "SET priority_score = ((priority_score - :minScore) / :range) * 100, " +
                                "last_calculation = :now " +
                                "WHERE user_id = :" + USER_ID_PARAM,
                        void.class)
                        .setParameter("minScore", minScore)
                        .setParameter("range", range)
                        .setParameter("now", LocalDateTime.now(ZoneOffset.UTC))
                        .setParameter(USER_ID_PARAM, userId)
                        .executeUpdate();
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    // Add to priorityDao
    public List<Object[]> findAllPriorityMappings() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                    "SELECT p.problem.problemId, p.user.userId, p.priorityId FROM UserProblemPriority p",
                    Object[].class)
                    .list();
        } finally {
            session.close();
        }
    }

    /**
     * Get highest priority problem of a specific difficulty
     */
    public Problem getHighestPriorityProblemOfDifficulty(User user, String difficulty) {
        try (Session session = sessionFactory.openSession()) {
            Query<Problem> q = session.createQuery("SELECT p FROM Problem p " +
                    "JOIN UserProblemPriority pp ON p = pp.problem " +
                    "WHERE pp.user = :user " +
                    "AND p.difficultyLevel = :difficulty " +
                    "ORDER BY pp.priorityScore DESC", Problem.class).setMaxResults(1);

            q.setParameter("user", user);
            q.setParameter("difficulty", difficulty);

            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Find all priorities for a user, ordered by priority score descending
     */
    public UserProblemPriority findNextChallengeByPriorityScoreDesc(User user) {
        Session session = sessionFactory.openSession();

        try {
            Query<UserProblemPriority> q = session.createNamedQuery("challenge.orderByPriority",
                    UserProblemPriority.class);
            q.setMaxResults(1);

            q.setParameter("user", user);

            return q.uniqueResult();

        } finally {
            session.close();
        }
    }

    /**
     * Find all priorities for a problem
     */
    public List<UserProblemPriority> findByProblem(Problem problem) {
        Session session = sessionFactory.openSession();

        try {
            Query<UserProblemPriority> q = session.createQuery(
                    "from UserProblemPriority where problem=:problem",
                    UserProblemPriority.class);
            q.setParameter("problem", problem);
            return q.list();

        } finally {
            session.close();
        }
    }

    /**
     * Find all priorities for a user
     */
    public List<UserProblemPriority> findByUser(User user) {
        Session session = sessionFactory.openSession();

        try {
            Query<UserProblemPriority> q = session.createQuery(
                    "from UserProblemPriority where user=:user",
                    UserProblemPriority.class);
            q.setParameter("user", user);
            return q.list();

        } finally {
            session.close();
        }
    }

    /**
     * Find all priorities
     */
    public List<UserProblemPriority> findAll() {
        Session session = sessionFactory.openSession();

        try {
            Query<UserProblemPriority> q = session.createQuery(
                    "from UserProblemPriority", UserProblemPriority.class);

            return q.list();
        } finally {
            session.close();
        }
    }

    public UserProblemPriority save(UserProblemPriority priority) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.persist(priority);
            session.getTransaction().commit();
            return priority;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    // Add this new batch save method to UserProblemPriorityDAO
    public void saveBatch(List<UserProblemPriority> priorities, int batchSize) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            for (int i = 0; i < priorities.size(); i++) {
                session.persist(priorities.get(i));

                // Flush and clear the session periodically
                if (i > 0 && i % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * Find min and max scores for a user
     * 
     * @return Object array with [minScore, maxScore, count]
     */
    public Object[] findMinMaxScoresByUserId(int userId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Object[]> query = session.createQuery(
                    "SELECT MIN(p.priorityScore), MAX(p.priorityScore), COUNT(p) " +
                            "FROM UserProblemPriority p " +
                            "WHERE p.user.userId = :" + USER_ID_PARAM,
                    Object[].class);
            query.setParameter(USER_ID_PARAM, userId);

            Object[] result = query.uniqueResult();

            // If there are no priorities or just one, return empty array
            if (result == null || result[2] == null || (Long) result[2] <= 1) {
                return new Object[0];
            }

            return result;
        } finally {
            session.close();
        }
    }

    /**
     * Get min and max scores for all users in one query
     * 
     * @return List of arrays containing [userId, minScore, maxScore, count]
     */
    public List<Object[]> findMinMaxScoresForAllUsers() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                    "SELECT p.user.userId, MIN(p.priorityScore), MAX(p.priorityScore), COUNT(p) " +
                            "FROM UserProblemPriority p " +
                            "GROUP BY p.user.userId",
                    Object[].class)
                    .list();
        } finally {
            session.close();
        }
    }

    /**
     * Find all priority IDs and scores for a user
     */
    public List<Object[]> findPriorityIdsAndScoresByUserId(int userId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Object[]> query = session.createQuery(
                    "SELECT p.priorityId, p.priorityScore " +
                            "FROM UserProblemPriority p " +
                            "WHERE p.user.userId = :" + USER_ID_PARAM,
                    Object[].class);
            query.setParameter(USER_ID_PARAM, userId);

            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Update batch of priorities using HQL to avoid entity issues
     */
    public void updateBatch(List<UserProblemPriority> priorities, int batchSize) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            for (UserProblemPriority priority : priorities) {
                session.createMutationQuery(
                        "UPDATE UserProblemPriority p " +
                                "SET p.priorityScore = :score, " +
                                "p.lastCalculation = :timestamp " +
                                "WHERE p.priorityId = :" + PRIORITY_ID_PARAM)
                        .setParameter("score", priority.getPriorityScore())
                        .setParameter("timestamp", priority.getLastCalculation())
                        .setParameter(PRIORITY_ID_PARAM, priority.getPriorityId())
                        .executeUpdate();
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public UserProblemPriority update(UserProblemPriority priority) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            priority = session.merge(priority); // Merges the state of the given object into the current persistence
                                                // context
            session.getTransaction().commit();
            return priority;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void updateAll(List<UserProblemPriority> allPriorities) {
        Session session = sessionFactory.openSession();
        int batchSize = 100;

        try {
            session.beginTransaction();

            for (int i = 0; i < allPriorities.size(); i++) {
                session.merge(allPriorities.get(i));

                // Flush and clear the session periodically
                if (i > 0 && i % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    /**
     * Delete a user problem priority
     */
    public void delete(UserProblemPriority userProblemPriority) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.remove(userProblemPriority);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }
}