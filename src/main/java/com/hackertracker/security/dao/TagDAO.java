package com.hackertracker.security.dao;

import com.hackertracker.security.dto.ProblemDTO;
import com.hackertracker.security.dto.UserProblemService;
import com.hackertracker.security.tag.Tag;
import com.hackertracker.security.dto.TagDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserProblemService userProblemService;

    public TagDAO(SessionFactory sessionFactory, UserProblemService userProblemService) {
        this.sessionFactory = sessionFactory;
        this.userProblemService = userProblemService;
    }

    public List<TagDTO> getAllTags(){
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Tag", Tag.class).list().stream().map((t) -> new TagDTO(t.getTagId(), t.getTagName())).toList();
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

    public List<TagDTO> getTagByName(String tagName) {
        Session session = sessionFactory.openSession();
//        Query<Tag> q = session.createQuery("from Tag where tagName=:tagName", Tag.class);
        Query<Tag> q = session.createQuery("from Tag where tagName LIKE :tagName", Tag.class);
        q.setParameter("tagName", "%" + tagName + "%");
//        q.setParameter("tagName", tagName);
//        return q.uniqueResult();
        return q.list().stream().map((t) -> new TagDTO(t.getTagId(), t.getTagName())).toList();
    }

    public List<ProblemDTO> getTagProblems(Tag tag) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            List<ProblemDTO> problems = tag.getListProblems().stream().map(
                    (problem) -> {
                        ProblemDTO problemDto = new ProblemDTO();
                        problemDto.setProblemId(problem.getProblemId());
                        problemDto.setPublicProblemId(problem.getPublicProblemId());
                        problemDto.setDifficultyLevel(problem.getDifficultyLevel());
                        problemDto.setPageUrl(problem.getPageUrl());
                        problemDto.setQuestionTitle(problem.getQuestionTitle());
                        problemDto.setTopics(userProblemService.getProblemTopics(problem));
                        problemDto.setAttempts(userProblemService.getProblemAttempts(problem, problemDto));
                        return problemDto;
                    }
            ).toList();

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
