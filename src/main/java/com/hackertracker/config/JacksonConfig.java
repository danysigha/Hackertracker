package com.hackertracker.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Don't fail on empty beans (like Hibernate proxies)
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        mapper.registerModule(new JavaTimeModule());

        // Handle bidirectional relationships
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

        return mapper;
    }
}