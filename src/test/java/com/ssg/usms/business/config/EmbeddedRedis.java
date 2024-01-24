package com.ssg.usms.business.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
@EnableRedisRepositories
public class EmbeddedRedis {

    private RedisServer redisServer;

    public EmbeddedRedis(RedisProperties redisProperties) {
        this.redisServer = RedisServer.builder()
                .port(redisProperties.getPort())
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