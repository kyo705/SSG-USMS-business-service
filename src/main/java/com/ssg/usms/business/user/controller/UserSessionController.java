package com.ssg.usms.business.user.controller;


import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.user.dto.HttpRequestModifyUserDto;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import com.ssg.usms.business.user.dto.SecurityState;
import com.ssg.usms.business.user.exception.NotAllowedSecondPasswordException;
import com.ssg.usms.business.user.service.UserService;
import com.ssg.usms.business.user.service.UserSessionService;
import com.ssg.usms.business.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.ssg.usms.business.user.constant.UserConstants.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserSessionController {

    private final UserSessionService userSessionService;
    private final UserService userService;

    private final JwtUtil jwtUtil;


    @GetMapping("/api/users/session")
    public ResponseEntity getSessionFromCurrentUser() throws IllegalAccessException {

        HttpResponseUserDto userDto = userSessionService.findUserBySession();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    @DeleteMapping("/api/users/{userId}")
    public ResponseEntity DeleteUser(@PathVariable (name = "userId") long userId,HttpServletRequest request) throws IllegalAccessException {

        validateCurrentUser(userId);
        userSessionService.deleteUser(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PatchMapping("/api/users/{userId}")
    public ResponseEntity modifyUserWithJwt(HttpServletRequest request, @Valid @RequestBody HttpRequestModifyUserDto dto, @PathVariable(name = "userId") Long userid) throws IllegalAccessException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        jwtUtil.VerifyToken(authorization,"Identification");
//        2차 비밀번호 , 시큐리티 레벨이 2인지 검증
        if(dto.getSecurityState().equals(SecurityState.SECONDPASSWORD) && !dto.getSecondPassword().matches(SECONDPASSWORD_PATTERN)){
            throw new NotAllowedSecondPasswordException(NOT_ALLOWED_SECONDPASSWORD_LITERAL);
        }
        if(!dto.getSecurityState().equals(SecurityState.SECONDPASSWORD) && !(dto.getSecondPassword() == null)){
            throw new NotAllowedSecondPasswordException(NOT_ALLOWED_SECONDPASSWORD_LITERAL);
        }

        validateCurrentUser(userid);

        userService.ModifyUser(userid,dto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION,authorization);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .headers(headers)
                .body(null);
    }

    private void validateCurrentUser(long userid) throws IllegalAccessException {

        UsmsUserDetails userDetails =(UsmsUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        log.info(String.valueOf(userDetails.getId()));
        log.info(String.valueOf(userid));

        if(userDetails.getId() != userid){
            throw new IllegalAccessException(NOT_MATCHED_SESSION_USRID_LITERAL);
        }
    }







}
