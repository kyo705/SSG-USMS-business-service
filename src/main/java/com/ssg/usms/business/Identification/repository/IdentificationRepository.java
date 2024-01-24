package com.ssg.usms.business.Identification.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
@Slf4j
public class IdentificationRepository {

    private final int LIMIT_TIME = 180;

    private final StringRedisTemplate redisTemplate;

    public void createSmsCertification(String keyAndCertificationNumber, String User){

        redisTemplate.opsForValue().set(keyAndCertificationNumber, User, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getIdentification(String key){

        return redisTemplate.opsForValue().get(key);
    }

    public void removeIdentification(String key){

        redisTemplate.delete(key);
    }

}

