package com.hackertracker.security.config;

import com.hackertracker.security.Schedule.Weekday;
import com.hackertracker.security.problem.Problem;
import com.hackertracker.security.problem.TagProblem;
import com.hackertracker.security.problem.TopicProblem;
import com.hackertracker.security.tag.Tag;
import com.hackertracker.security.topic.Topic;
import com.hackertracker.security.user.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class HibernateConfig {

    @Bean
    public SessionFactory sessionFactory() {
        Properties hibernateProps = new Properties();
        hibernateProps.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        hibernateProps.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/hack_bis?createDatabaseIfNotExist=true");
        hibernateProps.put("hibernate.connection.username", "root");
        hibernateProps.put("hibernate.connection.password", "root");
        hibernateProps.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        hibernateProps.put("hibernate.show_sql", "true");
        hibernateProps.put("hibernate.format_sql", "true");
        hibernateProps.put("hibernate.hbm2ddl.auto", "update");

        // Add Hibernate Search configuration
//        hibernateProps.put("hibernate.search.backend.directory.root", "./hibernate-search-indexes");

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(hibernateProps).build();

        return new MetadataSources(serviceRegistry)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Problem.class)
                .addAnnotatedClass(Weekday.class)
                .addAnnotatedClass(Tag.class)
                .addAnnotatedClass(TagProblem.class)
                .addAnnotatedClass(Topic.class)
                .addAnnotatedClass(TopicProblem.class)
                .addAnnotatedClass(UserCompletionPrediction.class)
                .addAnnotatedClass(UserProblemAttempt.class)
                .addAnnotatedClass(UserProblemPriority.class)
                .addAnnotatedClass(UserSchedule.class)
                .buildMetadata()
                .buildSessionFactory();
    }
}
