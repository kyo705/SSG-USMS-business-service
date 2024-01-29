package com.ssg.usms.business.video.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

import static com.ssg.usms.business.video.constant.VideoConstants.SET_NAME_OF_CONNECTED_STREAM_KEY;

@Repository
@RequiredArgsConstructor
public class RedisStreamKeyRepository implements StreamKeyRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public String saveStreamKey(String streamKey) {

        redisTemplate.opsForValue().set(streamKey, SET_NAME_OF_CONNECTED_STREAM_KEY);

        return streamKey;
    }

    @Override
    public String saveStreamKey(String streamKey, long expireTimeSecond) {

        redisTemplate.opsForValue().set(streamKey, SET_NAME_OF_CONNECTED_STREAM_KEY, expireTimeSecond, TimeUnit.SECONDS);

        return streamKey;
    }

    @Override
    public boolean isExistingStreamKey(String streamKey) {

        return redisTemplate.opsForValue().get(streamKey) != null;
    }

    @Override
    public void updateExpireTime(String streamKey, long expireTimeSecond) {

        redisTemplate.opsForValue().set(streamKey, SET_NAME_OF_CONNECTED_STREAM_KEY, expireTimeSecond, TimeUnit.SECONDS);
    }

    @Override
    public String removeStreamKey(String streamKey) {

        redisTemplate.delete(streamKey);

        return streamKey;
    }
}
