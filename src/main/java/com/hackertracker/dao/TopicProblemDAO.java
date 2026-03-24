package com.hackertracker.dao;

import com.hackertracker.problem.TopicProblem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class TopicProblemDAO {

    private final SessionFactory sessionFactory;

    public TopicProblemDAO(SessionFactory sessionFactory) {this.sessionFactory = sessionFactory;}

    /**
     * Save a new TopicProblem entity
     */
    public void saveTopicProblem(TopicProblem topicProblem) {
//        System.out.println("The user we want to save \n" + user);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(topicProblem);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
