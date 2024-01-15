package com.ssg.usms.business.video.repository;

import com.ssg.usms.business.config.EmbeddedRedis;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static com.ssg.usms.business.video.constant.VideoConstants.SET_NAME_OF_CONNECTED_STREAM_KEY;

@ActiveProfiles("test")
@SpringBootTest(classes = EmbeddedRedis.class)
public class RedisStreamKeyRepositoryWithIsExistingStreamKeyTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private StreamKeyRepository redisStreamKeyRepository;

    @DisplayName("이미 연결된 스트림 키에 대해 존재 여부 요청시 true를 리턴한다.")
    @Test
    public void testIsExistingStreamKeyWithAlreadyConnectedStreamKey() {

        //given
        String streamKey = "streamKey1";

        stringRedisTemplate.opsForSet().add(SET_NAME_OF_CONNECTED_STREAM_KEY, streamKey);

        //when
        boolean isExisting = redisStreamKeyRepository.isExistingStreamKey(streamKey);

        Assertions.assertThat(isExisting).isTrue();
    }

    @DisplayName("아직 연결되지 않은 스트림 키에 대해 존재 여부 요청시 false를 리턴한다.")
    @Test
    public void testIsExistingStreamKeyWithNotYetConnectedStreamKey() {

        //given
        String streamKey = "streamKey1";

        //when
        boolean isExisting = redisStreamKeyRepository.isExistingStreamKey(streamKey);

        Assertions.assertThat(isExisting).isFalse();
    }
}
