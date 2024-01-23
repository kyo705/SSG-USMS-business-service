package com.ssg.usms.business.video.repository;

import com.ssg.usms.business.config.EmbeddedRedis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.ssg.usms.business.video.constant.VideoConstants.SET_NAME_OF_CONNECTED_STREAM_KEY;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(classes = EmbeddedRedis.class)
public class RedisStreamKeyRepositoryWithIsExistingStreamKeyTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private StreamKeyRepository redisStreamKeyRepository;

    @BeforeEach
    public void setup() {
        Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()).getConnection().flushAll();
    }

    @DisplayName("[saveStreamKey] : 특정 value를 저장한다.")
    @Test
    public void testSaveStreamKey() {

        //given
        String streamKey = "streamKey1";

        assertThat(stringRedisTemplate.opsForSet().isMember(SET_NAME_OF_CONNECTED_STREAM_KEY, streamKey))
                .isFalse();

        //when
        redisStreamKeyRepository.saveStreamKey(streamKey);

        //then
        assertThat(stringRedisTemplate.opsForSet().isMember(SET_NAME_OF_CONNECTED_STREAM_KEY, streamKey))
                .isTrue();
    }

    @DisplayName("[isExistingStreamKey] : 이미 연결된 스트림 키에 대해 존재 여부 요청시 true를 리턴한다.")
    @Test
    public void testIsExistingStreamKeyWithAlreadyConnectedStreamKey() {

        //given
        String streamKey = "streamKey1";

        stringRedisTemplate.opsForSet().add(SET_NAME_OF_CONNECTED_STREAM_KEY, streamKey);

        //when
        boolean isExisting = redisStreamKeyRepository.isExistingStreamKey(streamKey);

        //then
        assertThat(isExisting).isTrue();
    }

    @DisplayName("[isExistingStreamKey] : 아직 연결되지 않은 스트림 키에 대해 존재 여부 요청시 false를 리턴한다.")
    @Test
    public void testIsExistingStreamKeyWithNotYetConnectedStreamKey() {

        //given
        String streamKey = "streamKey1";

        //when
        boolean isExisting = redisStreamKeyRepository.isExistingStreamKey(streamKey);

        //then
        assertThat(isExisting).isFalse();
    }

    @DisplayName("[removeStreamKey] : 특정 value를 삭제한다.")
    @Test
    public void testRemoveStreamKey() {

        //given
        String streamKey = "streamKey1";

        assertThat(stringRedisTemplate.opsForSet().isMember(SET_NAME_OF_CONNECTED_STREAM_KEY, streamKey))
                .isFalse();

        stringRedisTemplate.opsForSet().add(SET_NAME_OF_CONNECTED_STREAM_KEY, streamKey);

        assertThat(stringRedisTemplate.opsForSet().isMember(SET_NAME_OF_CONNECTED_STREAM_KEY, streamKey))
                .isTrue();

        //when
        redisStreamKeyRepository.removeStreamKey(streamKey);

        //then
        assertThat(stringRedisTemplate.opsForSet().isMember(SET_NAME_OF_CONNECTED_STREAM_KEY, streamKey))
                .isFalse();
    }
}
