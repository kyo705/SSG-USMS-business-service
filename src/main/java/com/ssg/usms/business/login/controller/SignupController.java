package com.ssg.usms.business.login.controller;


import com.ssg.usms.business.login.annotation.HttpRequestSignUpDTO;
import com.ssg.usms.business.login.persistence.HttpRequestSignUpDto;
import com.ssg.usms.business.login.Repository.UserRepository;
import com.ssg.usms.business.login.service.SignUpService;
import com.ssg.usms.business.login.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignupController {

    private final SignUpService signUpService;
    private final JwtUtil jwtUtil;

    @PostMapping("/users")
    public ResponseEntity<Void> users(@HttpRequestSignUpDTO @RequestBody HttpRequestSignUpDto httpRequestSignUpDto, HttpServletRequest request){

        System.out.println("/api/users  POST ---------");

        jwtUtil.VerifyToken(request);

        ResponseEntity res = signUpService.SignUp(httpRequestSignUpDto);

        return res;
    }

}
