package com.ssg.usms.business.user.controller;


import com.ssg.usms.business.user.dto.HttpRequestModifyUserDto;
import com.ssg.usms.business.user.dto.HttpRequestSignUpDto;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import com.ssg.usms.business.user.dto.SecurityState;
import com.ssg.usms.business.user.exception.NotAllowedKeyExcetpion;
import com.ssg.usms.business.user.exception.NotAllowedSecondPasswordException;
import com.ssg.usms.business.user.service.SignUpService;
import com.ssg.usms.business.user.service.UserService;
import com.ssg.usms.business.user.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.ssg.usms.business.user.constant.UserConstants.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final JwtUtil jwtUtil;

    private final UserService userService;
    private final SignUpService signUpService;

    @GetMapping("/users")
    public String users(){
        return "users ADMIN";
    }

    @PostMapping("/users")
    public ResponseEntity<Void> SignUp(@Valid @RequestBody HttpRequestSignUpDto httpRequestSignUpDto, HttpServletRequest request){

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        jwtUtil.VerifyToken(authorization,"Identification");

        signUpService.SignUp(httpRequestSignUpDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping("/user")
    public ResponseEntity FindUserWithJwt(HttpServletRequest request){

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        jwtUtil.VerifyToken(authorization,"Identification");

        HttpResponseUserDto userDto = userService.findUserByValue(authorization);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION,authorization);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(userDto);
    }

}
