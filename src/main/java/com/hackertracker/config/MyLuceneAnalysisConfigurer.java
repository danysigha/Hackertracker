package com.hackertracker.config;

import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

public class MyLuceneAnalysisConfigurer implements LuceneAnalysisConfigurer {
    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        context.analyzer("english").custom()
                .tokenizer("standard")
                .tokenFilter("lowercase")
                .tokenFilter("snowballPorter").param("language", "English")
                .tokenFilter("asciiFolding");

        context.analyzer("name").custom().tokenizer("standard").tokenFilter("lowercase")
                .tokenFilter("asciiFolding");

        // normalizer for keyword fields
        context.normalizer("lowercase").custom().tokenFilter("lowercase");
    }
}
