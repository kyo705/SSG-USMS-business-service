package com.ssg.usms.business.user.util;


import com.ssg.usms.business.user.exception.NotAllowedKeyExcetpion;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;


@Component
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret){
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

//    본인인증이면 code value
//    다른인증이 code key value



    public Claims getClaim(String token){

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
    public String verifyClaim(Claims claims,String value){

        return claims.get(value, String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }


//    토큰 발급.
    public String createJwt(HashMap<String , String> claims,Long expiredMs){

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    //  토큰 검증



}
