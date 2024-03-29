package com.ssg.usms.business.user.util;


import com.ssg.usms.business.user.exception.NotAllowedKeyExcetpion;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

import static com.ssg.usms.business.user.constant.UserConstants.NOT_ALLOWED_KEY_LITERAL;


@Component
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret){
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public Claims getClaim(String token){

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
    public String verifyClaim(Claims claims){

        return claims.getSubject();
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(HashMap<String , String> claims,Long expiredMs,String subject){

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public void VerifyToken(String authorization,String subject){

        if (authorization == null ){

            throw new NotAllowedKeyExcetpion(NOT_ALLOWED_KEY_LITERAL);
        }

        if (isExpired(authorization)){

            throw new NotAllowedKeyExcetpion(NOT_ALLOWED_KEY_LITERAL);
        }

        Claims claims = getClaim(authorization);

        if(!verifyClaim(claims).equals(subject)){

            throw new NotAllowedKeyExcetpion(NOT_ALLOWED_KEY_LITERAL);
        }

    }

}
