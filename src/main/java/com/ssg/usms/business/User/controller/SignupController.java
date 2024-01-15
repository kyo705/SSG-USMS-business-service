package com.ssg.usms.business.User.controller;


import com.ssg.usms.business.User.persistence.HttpRequestSignUpDto;
import com.ssg.usms.business.User.service.SignUpService;
import com.ssg.usms.business.User.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/users")
    public String users(){
        return "users ADMIN";
    }

    @PostMapping("/users")
    public ResponseEntity<Void> users(@Valid @RequestBody HttpRequestSignUpDto httpRequestSignUpDto, HttpServletRequest request){

        jwtUtil.VerifyToken(request);

        signUpService.SignUp(httpRequestSignUpDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
