package com.hackertracker.security.dao;

import com.hackertracker.security.dto.*;
import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.user.User;
import com.hackertracker.security.user.UserProblemPriority;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    UserProblemService userProblemService;


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
    public List<UserProblemPriorityDTO> findByUserOrderByPriorityScoreDesc(User user) {
        Session session = sessionFactory.openSession();

        try {
            Query<UserProblemPriority> q = session.createQuery(
                    "from UserProblemPriority where user=:user order by priorityScore desc",
                    UserProblemPriority.class
            );
            q.setParameter("user", user);

            return q.list().stream().map(
                    (priority) -> {
                        UserProblemPriorityDTO userProblemPriorityDto = new UserProblemPriorityDTO();
                        userProblemPriorityDto.setPriorityId(priority.getPriorityId());
                        userProblemPriorityDto.setPriorityScore(priority.getPriorityScore());
                        userProblemPriorityDto.setLastCalculation(priority.getLastCalculation());
                        userProblemPriorityDto.setLastAttempted(priority.getLastAttempted());
                        userProblemPriorityDto.setUserDto(userProblemService.getUserDto(priority.getUser()));
                        userProblemPriorityDto.setProblemDto(userProblemService.getProblemDto(priority.getProblem()));

                        return userProblemPriorityDto;
                    }
            ).toList();

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
     * Find all priorities
     */
    public List<UserProblemPriorityDTO> findAll() {
        Session session = sessionFactory.openSession();

        try {
            Query<UserProblemPriority> q = session.createQuery(
                    "from UserProblemPriority", UserProblemPriority.class);


            return q.list().stream().map(
                    (upp) -> {
                        UserProblemPriorityDTO userProblemPriorityDto = new UserProblemPriorityDTO();

                        ProblemDTO problemDto = userProblemService.getProblemDto(upp.getProblem());
                        UserDTO userDto = userProblemService.getUserDto(upp.getUser());

                        userProblemPriorityDto.setPriorityId(upp.getPriorityId());
                        userProblemPriorityDto.setPriorityScore(upp.getPriorityScore());
                        userProblemPriorityDto.setLastCalculation(upp.getLastCalculation());
                        userProblemPriorityDto.setLastAttempted(upp.getLastAttempted());
                        userProblemPriorityDto.setProblemDto(problemDto);
                        userProblemPriorityDto.setUserDto(userDto);

                        return userProblemPriorityDto;
                    }

            ).toList();
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

    public void saveAll( List<UserProblemPriority> allPriorities) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            for(UserProblemPriority priority : allPriorities) {
                session.persist(priority);
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