package com.example.backend.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class VectorStoreConfig {

    @Bean
    public JedisPooled jedisPooled(
            @Value("${proPath.redis.host:localhost}") String host,
            @Value("${proPath.redis.port:6379}") int port) {
        return new JedisPooled(host, port);
    }

    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    public VectorStore vectorStore(
            JedisPooled jedisPooled,
            EmbeddingModel embeddingModel,
            @Value("${spring.ai.vectorstore.redis.index-name:spring-ai-index}") String indexName,
            @Value("${spring.ai.vectorstore.redis.prefix:embedding:}") String prefix,
            @Value("${spring.ai.vectorstore.redis.initialize-schema:false}") boolean initializeSchema) {

        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName(indexName)
                .prefix(prefix)
                .initializeSchema(initializeSchema)
                .build();
    }
}
