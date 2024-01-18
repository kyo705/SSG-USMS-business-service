package com.ssg.usms.business.SignUp;


import com.ssg.usms.business.user.exception.NotAllowedKeyExcetpion;
import com.ssg.usms.business.user.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    @Test
    public void jwtCreateTest() {

        HashMap<String , String> hashMap = new HashMap<>();
        hashMap.put("code","1");
        hashMap.put("value", "010-4046-7715");



        String jwt = jwtUtil.createJwt(hashMap, 1800L,"Identification");

        assertNotNull(jwt, "jwt에 null이 들어가면 안된다.");
    }

    @DisplayName("올바른 코드와 값이 들어갔을때 검증")
    @Test
    void getCodeAndValueFromValidToken() {

        ArrayList<String> str = new ArrayList<>();
        str.add("code");
        str.add("value");

        HashMap<String , String> hashMap = new HashMap<>();
        hashMap.put(str.get(0),"0");
        hashMap.put(str.get(1),"tkfka123@gmail.com");
        Long expiredMs = 3600000L; // 1 hour
        String token = jwtUtil.createJwt(hashMap, expiredMs,"Identification");


        Claims claims= jwtUtil.getClaim(token);
        assertThat(jwtUtil.verifyClaim(claims)).isEqualTo("Identification");

    }

    @DisplayName("토큰의 유효기간이 유효할때")
    @Test
    void isExpiredWithValidToken() {

        ArrayList<String> str = new ArrayList<>();
        str.add("code");
        str.add("value");

        HashMap<String , String> hashMap = new HashMap<>();
        hashMap.put(str.get(0),"0");
        hashMap.put(str.get(1),"tkfka123@gmail.com");
        // Given
        Long expiredMs = 3600000L; // 1 hour
        String token = jwtUtil.createJwt(hashMap, expiredMs,"Identification");

        // When
        boolean result = jwtUtil.isExpired(token);

        // Then
        assertFalse(result);
    }

    @DisplayName("토큰의 유효기간이 유효하지않을때 ExpiredJwtExcetpion")
    @Test
    void isExpiredWithExpiredToken() {

        ArrayList<String> str = new ArrayList<>();
        str.add("code");
        str.add("value");

        HashMap<String , String> hashMap = new HashMap<>();
        hashMap.put(str.get(0),"0");
        hashMap.put(str.get(1),"tkfka123@gmail.com");
        // Given
        Long expiredMs = 0L;
        String token = jwtUtil.createJwt(hashMap, expiredMs,"Identification");

        // When Then
        assertThrows(ExpiredJwtException.class,() -> jwtUtil.isExpired(token));
    }


}
