package com.ssg.usms.business.SignUp;


import com.ssg.usms.business.login.exception.NotAllowedKeyExcetpion;
import com.ssg.usms.business.login.service.SignUpService;
import com.ssg.usms.business.login.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;
    @Mock
    private HttpServletRequest request;

    @Value("${spring.jwt.secret}")
    private String secret;
    @DisplayName("jwt토큰 을 반환하는 createJwt테스트")
    public void jwtCreateTest() {

        String jwt = jwtUtil.createJwt("0", "ans109912@asdf.com", 1800L);

        assertNotNull(jwt, "jwt에 null이 들어가면 안된다.");
    }

    @DisplayName("올바른 코드와 값이 들어갔을때 검증")
    @Test
    void getCodeAndValueFromValidToken() {

        String code = "0";
        String value = "tkfka123@gmail.com";
        Long expiredMs = 3600000L; // 1 hour
        String token = jwtUtil.createJwt(code, value, expiredMs);

        String extractedCode = jwtUtil.getCode(token);
        String extractedValue = jwtUtil.getValue(token);

        assertEquals(code, extractedCode);
        assertEquals(value, extractedValue);
    }

    @DisplayName("토큰의 유효기간이 유효할때")
    @Test
    void isExpiredWithValidToken() {
        // Given
        Long expiredMs = 3600000L; // 1 hour
        String token = jwtUtil.createJwt("code", "value", expiredMs);

        // When
        boolean result = jwtUtil.isExpired(token);

        // Then
        assertFalse(result);
    }

    @DisplayName("토큰의 유효기간이 유효하지않을때 ExpiredJwtExcetpion")
    @Test
    void isExpiredWithExpiredToken() {
        // Given
        Long expiredMs = 0L;
        String token = jwtUtil.createJwt("code", "value", expiredMs);

        // When Then
        assertThrows(ExpiredJwtException.class,() -> jwtUtil.isExpired(token));
    }

    @DisplayName("올바른 헤더로 들어왔을때")
    @Test
    void verifyTokenWithValidToken() {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtUtil.createJwt("code", "value", 3600000L));

        boolean result = jwtUtil.VerifyToken(request);

        assertTrue(result);
    }

    @DisplayName("제대로된 헤더로 들어왔지만 토큰의 유효기간이 expired됐을때")
    @Test
    void verifyTokenWithExpiredToken() {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtUtil.createJwt("code", "value", 0L));

        // When and Then
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.VerifyToken(request));
    }

    @DisplayName("헤더에 Bearer이아닌 다른값이 들어갔을때")
    @Test
    void verifyTokenWithInvalidAuthorizationHeader() {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader " + jwtUtil.createJwt("code", "value", 3600000L));

        // When and Then
        assertThrows(NotAllowedKeyExcetpion.class, () -> jwtUtil.VerifyToken(request));
    }




}
