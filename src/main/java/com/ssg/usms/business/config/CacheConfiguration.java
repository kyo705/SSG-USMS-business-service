package com.ssg.usms.business.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfiguration {

    public static final String FILE_NAME_LIST_CACHE_KEY = "VIDEO_FILE_NAME_LIST_KEY";
    public static final String IMG_FILE_CACHE_KEY = "IMG_FILE_KEY";

    @Value("${usms.cache.transcode-filename.ttl}")
    private long filenameListTtl;

    @Value("${usms.cache.img-file.ttl}")
    private long imgFileTtl;

    @Bean
    public CacheManager usmsCacheManager(RedisConnectionFactory connectionFactory) {

        Map<String, RedisCacheConfiguration> entityCacheConfigs = new HashMap<>();

        entityCacheConfigs.put(FILE_NAME_LIST_CACHE_KEY,
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(filenameListTtl)));
        entityCacheConfigs.put(IMG_FILE_CACHE_KEY,
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(imgFileTtl)));

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(entityCacheConfigs)
                .build();
    }
}
