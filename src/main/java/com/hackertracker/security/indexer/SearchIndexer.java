package com.hackertracker.security.indexer;

import com.hackertracker.security.problem.Problem;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;


@Component
public class SearchIndexer {
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(SearchIndexer.class);

    @Value("${hibernate.search.index.directory}")
    private String indexBaseDir;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void rebuildIndexAfterStartup() throws InterruptedException {
        logger.info("Rebuilding search index on application startup...");
        SearchSession searchSession = Search.session(entityManager);
        searchSession.massIndexer(Problem.class)
                .threadsToLoadObjects(7)
                .startAndWait();
        logger.info("Index rebuild completed on startup");
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

    // Extracted common method for building the index
    private void buildIndex() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        SearchSession searchSession = Search.session(entityManager);
        searchSession.massIndexer(Problem.class)
                .threadsToLoadObjects(7)
                .startAndWait();

        long endTime = System.currentTimeMillis();
        logger.info("Indexing completed in {} ms", (endTime - startTime));
    }
}

//@Component
//public class SearchIndexer {
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    private static final Logger logger = LoggerFactory.getLogger(SearchIndexer.class);
//
//    @Value("${hibernate.search.index.directory}")
//    private String indexBaseDir;
//
//    @PostConstruct
//    public void checkAndBuildIndex() throws InterruptedException {
//        File indexDir = new File(indexBaseDir);
//        // Only build indexes if they don't exist
//        if (!indexDir.exists() || indexDir.list().length == 0) {
//            logger.info("Index directory empty or not found. Starting initial indexing...");
//            long startTime = System.currentTimeMillis();
//
//            SearchSession searchSession = Search.session(entityManager);
//            searchSession.massIndexer(Problem.class)
//                    .threadsToLoadObjects(7)
//                    .startAndWait();
//
//            long endTime = System.currentTimeMillis();
//            logger.info("Initial indexing completed in {} ms", (endTime - startTime));
//        } else {
//            logger.info("Index directory found. Skipping initial indexing.");
//        }
//    }
//}