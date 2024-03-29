package com.ssg.usms.business.user.controller;


import com.ssg.usms.business.Identification.dto.CertificationCode;
import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.user.dto.HttpRequestModifyUserDto;
import com.ssg.usms.business.user.dto.HttpRequestSignUpDto;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import com.ssg.usms.business.user.dto.SecurityState;
import com.ssg.usms.business.user.exception.NotAllowedFormCheckException;
import com.ssg.usms.business.user.exception.NotAllowedSecondPasswordException;
import com.ssg.usms.business.user.exception.NotMatchedDtoJwtValueException;
import com.ssg.usms.business.user.service.UserService;
import com.ssg.usms.business.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static com.ssg.usms.business.Identification.constant.IdentificationConstant.*;
import static com.ssg.usms.business.user.constant.UserConstants.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    private final JwtUtil jwtUtil;

    private final UserService userService;

    @GetMapping("/api/users")
    public String users() {
        return "users ADMIN";
    }

    @PostMapping("/api/users")
    public ResponseEntity<Void> signUp(@Valid @RequestBody HttpRequestSignUpDto httpRequestSignUpDto, HttpServletRequest request) {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        int code = Integer.parseInt((String) jwtUtil.getClaim(authorization).get(IDENTIFICATION_CODE));
        String value = (String) jwtUtil.getClaim(authorization).get(IDENTIFICATION_VALUE);

        jwtUtil.VerifyToken(authorization, IDENTIFICATION_JWT_SUBJECT);

        if (code == CertificationCode.SMS.getCode()){

            if(!httpRequestSignUpDto.getPhoneNumber().equals(value)){
                throw new NotMatchedDtoJwtValueException(NOT_MATCHED_JWT_DTO_LITERAL);
            }
        }
        if (code == CertificationCode.EMAIL.getCode()){

            if(!httpRequestSignUpDto.getEmail().equals(value)){
                throw new NotMatchedDtoJwtValueException(NOT_MATCHED_JWT_DTO_LITERAL);
            }
        }


        userService.signUp(httpRequestSignUpDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping("/api/user")
    public ResponseEntity<List<HttpResponseUserDto>> findUserWithJwt(HttpServletRequest request) {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        jwtUtil.VerifyToken(authorization, IDENTIFICATION_JWT_SUBJECT);

        List<HttpResponseUserDto> userDto = userService.findUserByValue(authorization);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, authorization);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(userDto);
    }

    @GetMapping("/api/users/session")
    public ResponseEntity<HttpResponseUserDto> getSessionFromCurrentUser() throws IllegalAccessException {

        HttpResponseUserDto userDto = userService.findUserBySession();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    @DeleteMapping("/api/users/{userId}")
    public ResponseEntity<HttpResponseUserDto> deleteUser(@PathVariable(name = "userId") long userId, HttpServletRequest request) throws IllegalAccessException {

        validateCurrentUser(userId);
        userService.deleteUser(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PatchMapping("/api/users/{userId}")
    public ResponseEntity<HttpResponseUserDto> modifyUserWithJwt(HttpServletRequest request, @Valid @RequestBody HttpRequestModifyUserDto dto, @PathVariable(name = "userId") Long userid) throws IllegalAccessException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        jwtUtil.VerifyToken(authorization, IDENTIFICATION_JWT_SUBJECT);

        if (dto.getSecurityState().equals(SecurityState.SECONDPASSWORD) && !dto.getSecondPassword().matches(SECONDPASSWORD_PATTERN)) {
            throw new NotAllowedSecondPasswordException(NOT_ALLOWED_SECONDPASSWORD_LITERAL);
        }
        if (!dto.getSecurityState().equals(SecurityState.SECONDPASSWORD) && !(dto.getSecondPassword() == null)) {
            throw new NotAllowedSecondPasswordException(NOT_ALLOWED_SECONDPASSWORD_LITERAL);
        }

        validateCurrentUser(userid);

        userService.modifyUser(userid, dto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, authorization);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .headers(headers)
                .body(null);
    }

    @GetMapping("/api/check/users")
    public ResponseEntity<Void> checkDuplicateUserInfo(@RequestParam(required = false) String email,
                                                 @RequestParam(required = false) String phoneNumber,
                                                 @RequestParam(required = false) String username) {
        if(username ==null && email == null && phoneNumber == null){
            throw new NotAllowedFormCheckException(NOT_ALLOWED_FORM_LITERAL);
        }
        if (username != null) {
            if (!username.matches(USERNAME_PATTERN)) {

                throw new NotAllowedFormCheckException(NOT_ALLOWED_FORM_LITERAL);
            }
        }
        if (email != null) {
            if (!email.matches(EMAIL_PATTERN)) {

                throw new NotAllowedFormCheckException(NOT_ALLOWED_FORM_LITERAL);
            }
        }
        if (phoneNumber != null) {
            if (!phoneNumber.matches(PHONENUMBER_PATTERN)) {

                throw new NotAllowedFormCheckException(NOT_ALLOWED_FORM_LITERAL);
            }
        }

        userService.checkExistUser(email, username, phoneNumber);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    private void validateCurrentUser(long userid) throws IllegalAccessException {

        try {
            UsmsUserDetails userDetails = (UsmsUserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            if (userDetails.getId() != userid) {
                throw new IllegalAccessException(NOT_MATCHED_SESSION_USRID_LITERAL);
            }
        } catch (ClassCastException | NullPointerException e) {
            throw new IllegalAccessException(NOT_MATCHED_SESSION_USRID_LITERAL);
        }
    }


}
