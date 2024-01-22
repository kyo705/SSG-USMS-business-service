package com.ssg.usms.business.video.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import static com.ssg.usms.business.video.constant.VideoConstants.SET_NAME_OF_CONNECTED_STREAM_KEY;

@Repository
@RequiredArgsConstructor
public class RedisStreamKeyRepository implements StreamKeyRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public String saveStreamKey(String streamKey) {

        redisTemplate.opsForSet().add(SET_NAME_OF_CONNECTED_STREAM_KEY, streamKey);

        return streamKey;
    }

    @Override
    public boolean isExistingStreamKey(String streamKey) {

        return Boolean.TRUE.equals(
                redisTemplate
                        .opsForSet()
                        .isMember(SET_NAME_OF_CONNECTED_STREAM_KEY, streamKey)
        );
    }

    @Override
    public String removeStreamKey(String streamKey) {

        redisTemplate
                .opsForSet()
                .remove(SET_NAME_OF_CONNECTED_STREAM_KEY, streamKey);

        return streamKey;
    }
}
