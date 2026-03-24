package com.hackertracker.dao;

import com.hackertracker.dto.TopicDTO;
import com.hackertracker.problem.Problem;
import com.hackertracker.topic.Topic;
import com.hackertracker.user.User;
import com.hackertracker.user.UserTopics;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author danysigha
 */
@Component
@Repository
public class TopicDAO {

    private final SessionFactory sessionFactory;
    private final UserTopicsDAO userTopicsDao;
    private final UserDAO userDao;

    public TopicDAO(SessionFactory sessionFactory, UserTopicsDAO userTopicsDao, UserDAO userDao) {
        this.sessionFactory = sessionFactory;
        this.userTopicsDao = userTopicsDao;
        this.userDao = userDao;
    }

    /**
     * Save a new Topic entity
     */
    public void saveTopicByUserId(Topic topic, int userId) {
//        System.out.println("The user we want to save \n" + user);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(topic);

                UserTopics userTopics = userTopicsDao.getUserTopicsByUserId(userId);
                User myUser = userDao.getUserByIdWithCollections(userId);

                if(userTopics != null) {
                    userTopics.getTopics().addLast((byte) (userTopics.getTopics().size() + 1));
                    myUser.setTopicRanks(userTopics);
                    userDao.updateUser(myUser);
                    tx.commit();
                }
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    public List<Topic> getAllTopics(){
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Topic order by topicRank", Topic.class).list();
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Topic> getTopicsPage(int pageNumber, int pageSize) {
        Session session = sessionFactory.openSession();
        try {
            Query<Topic> query = session.createQuery("FROM Topic t ORDER BY t.topicId", Topic.class);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        } finally {
            session.close();
        }
    }

    public List<TopicDTO> getAllTopicsDtos(){
        try (Session session = sessionFactory.openSession()) {
            List<Topic> topics = session.createQuery("FROM Topic order by topicRank", Topic.class).list();
            return topics.stream().map(TopicDTO::fromEntity).toList();
        } catch (Exception e) {
            throw e;
        }
    }

    public Topic getTopicById(byte topicId) {
        Session session = sessionFactory.openSession();
        Query<Topic> q = session.createQuery("from Topic where topicId=:topicId", Topic.class);
        q.setParameter("topicId", topicId);
        return q.uniqueResult();
    }

    public List<Topic> getTopicByName(String topicName) {
        Session session = sessionFactory.openSession();
        Query<Topic> q = session.createQuery("from Topic where topicName LIKE :topicName", Topic.class);
        q.setParameter("topicName", "%" + topicName + "%");
        return q.list();
    }

    public List<Problem> getTopicProblems(Topic topic) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            List<Problem> topicProblems = topic.getListProblems();
            tx.commit();
            return topicProblems;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }


    /**
     * Update an existing topic entity
     */
    public void updateTopic(Topic topic) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.merge(topic);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}