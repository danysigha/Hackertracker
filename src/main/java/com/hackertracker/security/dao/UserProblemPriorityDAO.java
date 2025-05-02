package com.hackertracker.security.dao;

import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserProblemPriority;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for user problem priorities
 */
@Repository
public class UserProblemPriorityDAO {

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

        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
    }


    /**
     * Find all priorities for a user, ordered by priority score descending
     */
    public UserProblemPriority findNextChallengeByPriorityScoreDesc(User user) {
        Session session = sessionFactory.openSession();

        try {
            Query<UserProblemPriority> q = session.createNamedQuery("challenge.orderByPriority", UserProblemPriority.class);
            q.setMaxResults(1);

            q.setParameter("user", user);

            UserProblemPriority priority = q.uniqueResult();

            return priority;

        } catch (Exception e) {
            throw e;
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
                    UserProblemPriority.class
            );
            q.setParameter("problem", problem);
            return q.list();

        } catch (Exception e) {
            throw e;
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
                    UserProblemPriority.class
            );
            q.setParameter("user", user);
            return q.list();

        } catch (Exception e) {
            throw e;
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
        } catch (Exception e) {
            throw e;
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


    public UserProblemPriority update(UserProblemPriority priority) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            priority = session.merge(priority);  // Merges the state of the given object into the current persistence context
            session.getTransaction().commit();
            return priority;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }


    public void updateAll( List<UserProblemPriority> allPriorities) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            for(UserProblemPriority priority : allPriorities) {
                session.merge(priority);
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
     * Delete a user problem priority
     */
    public void delete(UserProblemPriority userProblemPriority) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.remove(userProblemPriority);
            session.getTransaction().commit();
        } catch(Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }
}