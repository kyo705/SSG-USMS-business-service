package com.ssg.usms.business.identification.redis;


import com.ssg.usms.business.Identification.repository.IdentificationRepository;

import com.ssg.usms.business.config.EmbeddedRedis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;


import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("test")
@SpringBootTest(classes = EmbeddedRedis.class)
public class redisTest {

    @Autowired
    private IdentificationRepository identificationRepository;

    private final String keyAndCertificationNumber = "verificationKey";
    private final String user = "jsonString";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DisplayName("redis서버에 키와 벨류형태로 성공적으로 저장한 경우")
    @Test
    public void testCreateSmsCertification() {
        // Arrange

        // Act
        identificationRepository.createSmsCertification(keyAndCertificationNumber, user);
        String stored = redisTemplate.opsForValue().get(keyAndCertificationNumber);

        // Assert
        assertThat(stored).isEqualTo(user);
    }

    @DisplayName("redis서버에서 키로 데이터를 성공적으로 가져온다.")
    @Test
    public void getSmsCertificationTest() {
        // Arrange
        String keyAndCertificationNumber = "verificationKey";
        String user = "jsonString";

        redisTemplate.opsForValue().set(keyAndCertificationNumber, user, Duration.ofSeconds(180));
        // Act
        String stored = identificationRepository.getIdentification(keyAndCertificationNumber);

        // Assert
        assertThat(stored).isEqualTo(user);
    }

    @DisplayName("redis서버에서 키가 존재하지 않는경우 null 리턴")
    @Test
    public void FailedgetSmsCertificationTest() {
        // Arrange
        String keyAndCertificationNumber = "verificationKe1y";
        String user = "jsonString";

        // Act
        String stored = identificationRepository.getIdentification(keyAndCertificationNumber);

        // Assert
        assertThat(stored).isEqualTo(null);
    }



    @DisplayName("redis서버에 성공적으로 데이터를 지운다.")
    @Test
    public void removeSmsCertificationTest() {
        // Arrange
        redisTemplate.opsForValue().set(keyAndCertificationNumber, user, Duration.ofSeconds(180));
        // Act
        identificationRepository.removeIdentification(keyAndCertificationNumber);
        // Assert
        assertFalse(redisTemplate.hasKey(keyAndCertificationNumber));
    }

    @DisplayName("redis서버에 데이터가 없는 경우 hasKey에서 null 리턴")
    @Test
    public void FailedremoveSmsCertificationTest() {

        assertThat(identificationRepository.getIdentification("2")).isEqualTo(null);
        identificationRepository.removeIdentification(keyAndCertificationNumber);
    }


// 타임 아웃 체크


} 
