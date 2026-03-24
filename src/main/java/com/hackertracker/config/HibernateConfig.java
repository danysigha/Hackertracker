package com.hackertracker.config;

import com.hackertracker.problem.Problem;
import com.hackertracker.problem.ProblemHistory;
import com.hackertracker.problem.TagProblem;
import com.hackertracker.problem.TopicProblem;
import com.hackertracker.tag.Tag;
import com.hackertracker.topic.Topic;
import com.hackertracker.user.*;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import java.util.Properties;


@Configuration
public class HibernateConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Value("${hibernate.show_sql:false}")
    private String showSql;

    @Value("${hibernate.format_sql:false}")
    private String formatSql;

    @Bean
    public CacheManager jCacheManager() {
        CacheManager cacheManager = Caching.getCachingProvider()
                .getCacheManager();

        // Define cache configuration for entities
        javax.cache.configuration.Configuration<Object, Object> entityCacheConfig =
                Eh107Configuration.fromEhcacheCacheConfiguration(
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                        Object.class, Object.class,
                                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                                .heap(1000, org.ehcache.config.units.EntryUnit.ENTRIES)
                                                .offheap(10, org.ehcache.config.units.MemoryUnit.MB))
                                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(
                                        java.time.Duration.ofMinutes(30))));

        // Define cache configuration for query cache
        javax.cache.configuration.Configuration<Object, Object> queryCacheConfig =
                Eh107Configuration.fromEhcacheCacheConfiguration(
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                        Object.class, Object.class,
                                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                                .heap(1000, org.ehcache.config.units.EntryUnit.ENTRIES))
                                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(
                                        java.time.Duration.ofMinutes(10))));

        // Define cache configuration for timestamp cache (no expiry)
        javax.cache.configuration.Configuration<Object, Object> timestampCacheConfig =
                Eh107Configuration.fromEhcacheCacheConfiguration(
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                        Object.class, Object.class,
                                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                                .heap(1000, org.ehcache.config.units.EntryUnit.ENTRIES))
                                .withExpiry(ExpiryPolicyBuilder.noExpiration()));

        // Create caches for entities
        cacheManager.createCache("com.hackertracker.problem.Problem", entityCacheConfig);
        cacheManager.createCache("com.hackertracker.tag.Tag", entityCacheConfig);
        cacheManager.createCache("com.hackertracker.topic.Topic", entityCacheConfig);

        // Create caches for collections
        cacheManager.createCache("com.hackertracker.problem.Problem.tags", entityCacheConfig);
        cacheManager.createCache("com.hackertracker.problem.Problem.topics", entityCacheConfig);

        // Create caches for query cache regions
        cacheManager.createCache("default-query-results-region", queryCacheConfig);
        cacheManager.createCache("default-update-timestamps-region", timestampCacheConfig);

        return cacheManager;
    }

    @Value("${hibernate.search.index.directory}")
    private String indexBaseDir;


    @Bean
    public SessionFactory sessionFactory(CacheManager jCacheManager) {
        Properties hibernateProps = new Properties();
        hibernateProps.put("hibernate.connection.driver_class", dbDriver);
        hibernateProps.put("hibernate.connection.url", dbUrl);
        hibernateProps.put("hibernate.connection.username", dbUsername);
        hibernateProps.put("hibernate.connection.password", dbPassword);
        hibernateProps.put("hibernate.dialect", hibernateDialect);
        hibernateProps.put("hibernate.show_sql", showSql);
        hibernateProps.put("hibernate.format_sql", formatSql);
        hibernateProps.put("hibernate.hbm2ddl.auto", "update");
        hibernateProps.put("hibernate.jdbc.time_zone", "UTC");
        hibernateProps.put("hibernate.jdbc.batch_size", 100);
        hibernateProps.put("hibernate.order_inserts", true);
        hibernateProps.put("hibernate.order_updates", true);
        hibernateProps.put("hibernate.jdbc.batch_versioned_data", true);
        hibernateProps.put("hibernate.connection.provider_disables_autocommit", true);
        hibernateProps.put("hibernate.connection.pool_size", "10"); // Adjust based on your needs
        hibernateProps.put("hibernate.default_batch_fetch_size", "50"); // Global batch fetching


        // Add Hibernate Search configuration
        hibernateProps.put("hibernate.search.backend.directory.root", indexBaseDir);
// Add this line to register your analyzer configurer
        hibernateProps.put("hibernate.search.backend.analysis.configurer", "com.hackertracker.config.MyLuceneAnalysisConfigurer");

        // Add this to your hibernateProps in the sessionFactory method
        hibernateProps.put("hibernate.current_session_context_class", "thread");

        // Second-level cache configuration
        hibernateProps.put("hibernate.cache.use_second_level_cache", "true");
        hibernateProps.put("hibernate.cache.use_query_cache", "true");
        hibernateProps.put("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.JCacheRegionFactory");
        hibernateProps.put("hibernate.jakarta.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider");

        // Share the JCache CacheManager
        hibernateProps.put("hibernate.jakarta.cache.cache_manager", jCacheManager);

        // Optional: statistics for monitoring cache performance
        hibernateProps.put("hibernate.generate_statistics", "true");

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(hibernateProps).build();

        return new MetadataSources(serviceRegistry)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Problem.class)
                .addAnnotatedClass(UserTopics.class)
                .addAnnotatedClass(Tag.class)
                .addAnnotatedClass(TagProblem.class)
                .addAnnotatedClass(Topic.class)
                .addAnnotatedClass(TopicProblem.class)
                .addAnnotatedClass(UserProblemCompletion.class)
                .addAnnotatedClass(UserProblemAttempt.class)
                .addAnnotatedClass(UserProblemPriority.class)
                .addAnnotatedClass(UserSchedule.class)
                .addAnnotatedClass(ProblemHistory.class)
                .buildMetadata()
                .buildSessionFactory();
    }
}
