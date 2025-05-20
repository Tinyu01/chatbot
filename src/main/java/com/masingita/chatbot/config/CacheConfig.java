package com.masingita.chatbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Redis caching configuration for improved performance.
 * Implements a time-to-live strategy for country data to ensure freshness.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${chatbot.cache.ttl-seconds:3600}")
    private long cacheTtlSeconds;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(cacheTtlSeconds))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .withCacheConfiguration("countries", 
                        cacheConfig.entryTtl(Duration.ofDays(1)))
                .withCacheConfiguration("countryDetails", 
                        cacheConfig.entryTtl(Duration.ofHours(12)))
                .withCacheConfiguration("conversationHistory", 
                        cacheConfig.entryTtl(Duration.ofDays(30)))
                .build();
    }
}