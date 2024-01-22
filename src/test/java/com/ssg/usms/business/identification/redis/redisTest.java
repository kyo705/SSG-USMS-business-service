package com.ssg.usms.business.identification.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
import com.ssg.usms.business.Identification.dto.SmsCertificationDao;

import com.ssg.usms.business.config.EmbeddedRedis;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;


import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@SpringBootTest(classes = EmbeddedRedis.class)
public class redisTest {

    @Autowired
    private SmsCertificationDao smsCertificationDao ;

    private final String keyAndCertificationNumber = "verificationKey";
    private final String user = "jsonString";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DisplayName("redis서버에 키와 벨류형태로 성공적으로 저장한 경우")
    @Test
    public void testCreateSmsCertification() {
        // Arrange

        // Act
        smsCertificationDao.createSmsCertification(keyAndCertificationNumber, user);
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
        String stored = smsCertificationDao.getSmsCertification(keyAndCertificationNumber);

        // Assert
        assertThat(stored).isEqualTo(user);
    }

    @DisplayName("redis서버에 성공적으로 데이터를 지운다.")
    @Test
    public void removeSmsCertificationTest() {
        // Arrange
        redisTemplate.opsForValue().set(keyAndCertificationNumber, user, Duration.ofSeconds(180));
        // Act
        smsCertificationDao.removeSmsCertification(keyAndCertificationNumber);
        // Assert
        assertFalse(redisTemplate.hasKey(keyAndCertificationNumber));
    }





} 
