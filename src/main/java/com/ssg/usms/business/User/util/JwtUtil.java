package com.ssg.usms.business.User.util;


import com.ssg.usms.business.User.exception.NotAllowedKeyExcetpion;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret){
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }
    public String getCode(String token){

        String code =Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("code",String.class);

        return code;
    }

    public String getValue(String token){

        String value =Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("value",String.class);

        return value;
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

//  토큰 검증
    public boolean VerifyToken(HttpServletRequest request){
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null || !authorization.startsWith("Bearer ")){

            System.out.println("tocken null");
            throw new NotAllowedKeyExcetpion("올바르지 못한 본인인증 키 입니다.");
        }

        String token = authorization.split(" ")[1];



        if (isExpired(token)){
//            토큰 만료
            System.out.println("token Expired");
            throw new NotAllowedKeyExcetpion("올바르지 못한 본인인증 키 입니다.");
        }
        try {
            getCode(token);
            getValue(token);
        }
        catch (Exception e){
            throw new NotAllowedKeyExcetpion("올바르지 못한 본인인증 키 입니다.");
        }
        return true;
    }


//    토큰 발급.
    public String createJwt(String code, String value, Long expiredMs){

        return Jwts.builder()
                .claim("code", code)
                .claim("value", value)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

}
