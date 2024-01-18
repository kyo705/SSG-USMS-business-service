package com.ssg.usms.business.user.controller;


import com.ssg.usms.business.user.dto.HttpRequestSignUpDto;
import com.ssg.usms.business.user.exception.NotAllowedKeyExcetpion;
import com.ssg.usms.business.user.service.SignUpService;
import com.ssg.usms.business.user.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

import static com.ssg.usms.business.user.constant.UserConstants.NOT_ALLOWED_KEY_LITERAL;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignupController {

    private final SignUpService signUpService;
    private final JwtUtil jwtUtil;

    @GetMapping("/users")
    public String users(){
        return "users ADMIN";
    }

    @PostMapping("/users")
    public ResponseEntity<Void> SignUp(@Valid @RequestBody HttpRequestSignUpDto httpRequestSignUpDto, HttpServletRequest request){

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        VerifyToken(authorization);

        signUpService.SignUp(httpRequestSignUpDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private void VerifyToken(String authorization){

        if (authorization == null ){

            throw new NotAllowedKeyExcetpion(NOT_ALLOWED_KEY_LITERAL);
        }

        if (jwtUtil.isExpired(authorization)){

            throw new NotAllowedKeyExcetpion(NOT_ALLOWED_KEY_LITERAL);
        }

        Claims claims = jwtUtil.getClaim(authorization);

        if(!jwtUtil.verifyClaim(claims).equals("Identification")){

            throw new NotAllowedKeyExcetpion(NOT_ALLOWED_KEY_LITERAL);
        }

    }




}
