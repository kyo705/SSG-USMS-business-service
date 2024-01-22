package com.ssg.usms.business.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class EmbeddedRedis {

    private RedisServer redisServer;

    public EmbeddedRedis(RedisProperties redisProperties) {
        this.redisServer = RedisServer.builder()
                .port(6379)
                .setting("maxmemory 10M").build();
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}