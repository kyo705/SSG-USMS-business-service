package com.ssg.usms.business.signUp;


import com.ssg.usms.business.user.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
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

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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




    @DisplayName("잘못된 시크릿 키가 들어왔을경우")
    @Test
    void getCodeAndValueFromInValidToken() {

        ArrayList<String> str = new ArrayList<>();
        str.add("code");
        str.add("value");

        HashMap<String , String> hashMap = new HashMap<>();
        hashMap.put(str.get(0),"0");
        hashMap.put(str.get(1),"tkfka123@gmail.com");
        Long expiredMs = 3600000L;

        String sec="vmfhaltmskdlstkfkdgodyroqkfwkdbalroqkfwkdbalaaaaaaaaaaaaaaaabbbbc";

        Key secret = new SecretKeySpec(sec.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

        String token =Jwts.builder()
                .claims(hashMap)
                .subject("Identification")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secret)
                .compact();

        assertThrows(SignatureException.class, () -> {
            jwtUtil.getClaim(token);
        });

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
