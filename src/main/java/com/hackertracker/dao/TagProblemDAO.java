package com.hackertracker.dao;

import com.hackertracker.problem.TagProblem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class TagProblemDAO {

    private final SessionFactory sessionFactory;

    public TagProblemDAO(SessionFactory sessionFactory) {this.sessionFactory = sessionFactory;}

    /**
     * Save a new TagProblem entity
     */
    public void saveTagProblem(TagProblem tagProblem) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(tagProblem);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
