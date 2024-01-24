package com.ssg.usms.business.config;

import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.util.SocketUtils;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class EmbeddedRedis {

    private RedisServer redisServer;

    public EmbeddedRedis(RedisProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getPort());
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }

    @AfterEach
    public void afterEach() throws InterruptedException {
        this.redisServer.stop();
        Thread.sleep(500);
    }
}