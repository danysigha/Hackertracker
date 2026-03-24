package com.hackertracker.indexer;

import com.hackertracker.problem.Problem;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class SearchIndexer {
    private static final Logger logger = LoggerFactory.getLogger(SearchIndexer.class);

    private final SessionFactory sessionFactory;

    @Value("${hibernate.search.index.directory}")
    private String indexBaseDir;

    public SearchIndexer(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void buildIndexAfterStartup() {
        logger.info("Rebuilding search index on application startup...");

        // Use Hibernate Session directly
        org.hibernate.Session session = sessionFactory.openSession();
        org.hibernate.Transaction tx = null;

        try {
            tx = session.beginTransaction();

            // Use the Hibernate Search API with the Session
            org.hibernate.search.mapper.orm.Search.session(session)
                    .massIndexer(Problem.class)
                    .threadsToLoadObjects(7)
                    .startAndWait();

            tx.commit();
            logger.info("Index rebuild completed on startup");
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to rebuild index", e);
        } finally {
            session.close();
        }
    }

    private void buildIndex() {
        org.hibernate.Session session = sessionFactory.openSession();
        org.hibernate.Transaction tx = null;
        long startTime = System.currentTimeMillis();

        try {
            tx = session.beginTransaction();

            org.hibernate.search.mapper.orm.Search.session(session)
                    .massIndexer(Problem.class)
                    .threadsToLoadObjects(7)
                    .startAndWait();

            tx.commit();

            long endTime = System.currentTimeMillis();
            logger.info("Indexing completed in {} ms", (endTime - startTime));
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to rebuild index", e);
        } finally {
            session.close();
        }
    }
}



//    @PostConstruct
//    @Transactional
//    public void checkAndBuildIndex() throws InterruptedException {
//        File indexDir = new File(indexBaseDir);
//        // Only build indexes if they don't exist
//        if (!indexDir.exists() || indexDir.list().length == 0) {
//            logger.info("Index directory empty or not found. Starting initial indexing...");
//            buildIndex();
//        } else {
//            logger.info("Index directory found. Skipping initial indexing.");
//        }
//    }

    // New method for manual rebuilding
//    @Transactional
//    public void rebuildIndex() throws InterruptedException {
//        logger.info("Starting to rebuild search index...");
//        // Optionally purge existing index first
//        SearchSession searchSession = Search.session(entityManager);
//        searchSession.workspace(Problem.class).purge();
//        logger.info("Existing index purged. Building new index...");
//
//        buildIndex();
//    }