package com.hackertracker.security.config;

//import com.hackertracker.security.Schedule.Weekday;
import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.problem.TagProblem;
import com.hackertracker.security.problem.TopicProblem;
import com.hackertracker.security.tag.Tag;
import com.hackertracker.security.topic.Topic;
import com.hackertracker.security.user.*;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import java.util.Properties;


@Configuration
public class HibernateConfig {

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
        cacheManager.createCache("com.hackertracker.security.problem.Problem", entityCacheConfig);
        cacheManager.createCache("com.hackertracker.security.tag.Tag", entityCacheConfig);
        cacheManager.createCache("com.hackertracker.security.topic.Topic", entityCacheConfig);

        // Create caches for collections
        cacheManager.createCache("com.hackertracker.security.problem.Problem.tags", entityCacheConfig);
        cacheManager.createCache("com.hackertracker.security.problem.Problem.topics", entityCacheConfig);

        // Create caches for query cache regions
        cacheManager.createCache("default-query-results-region", queryCacheConfig);
        cacheManager.createCache("default-update-timestamps-region", timestampCacheConfig);

        return cacheManager;
    }

    @Bean
    public SessionFactory sessionFactory(CacheManager jCacheManager) {
        Properties hibernateProps = new Properties();
        hibernateProps.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        hibernateProps.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/hack_bis?createDatabaseIfNotExist=true");
        hibernateProps.put("hibernate.connection.username", "root");
        hibernateProps.put("hibernate.connection.password", "root");
        hibernateProps.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        hibernateProps.put("hibernate.show_sql", "true");
        hibernateProps.put("hibernate.format_sql", "true");
        hibernateProps.put("hibernate.hbm2ddl.auto", "update");
        hibernateProps.put("hibernate.jdbc.time_zone", "UTC");


        // Add Hibernate Search configuration
//        hibernateProps.put("hibernate.search.backend.directory.root", "./hibernate-search-indexes");

        /// Second-level cache configuration
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
                .buildMetadata()
                .buildSessionFactory();
    }
}
