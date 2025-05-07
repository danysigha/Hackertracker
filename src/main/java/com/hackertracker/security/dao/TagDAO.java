package com.hackertracker.security.dao;

import com.hackertracker.security.dto.TagDTO;
import com.hackertracker.security.dto.TopicDTO;
import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.tag.Tag;
import com.hackertracker.security.topic.Topic;
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
public class TagDAO {

    private final SessionFactory sessionFactory;

    public TagDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Tag> getAllTags(){
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Tag", Tag.class).list();
        } catch (Exception e) {
            throw e;
        }
    }

    public List<TagDTO> getAllTagDtos(){
        try (Session session = sessionFactory.openSession()) {
            List<Tag> tags = session.createQuery("FROM Tag", Tag.class).list();
            return tags.stream().map(TagDTO::fromEntity).toList();
        } catch (Exception e) {
            throw e;
        }
    }

    public Tag getTagById(byte tagId) {
        Session session = sessionFactory.openSession();
        Query<Tag> q = session.createQuery("from Tag where tagId=:tagId", Tag.class);
        q.setParameter("tagId", tagId);
        return q.uniqueResult();
    }

    public List<Tag> getTagByName(String tagName) {
        Session session = sessionFactory.openSession();
        Query<Tag> q = session.createQuery("from Tag where tagName LIKE :tagName", Tag.class);
        q.setParameter("tagName", "%" + tagName + "%");
        return q.list();
    }

    public List<Problem> getTagProblems(Tag tag) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            List<Problem> problems = tag.getListProblems();
            tx.commit();
            return problems;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

}
