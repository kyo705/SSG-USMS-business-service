package com.ssg.usms.business.security.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.security.login.persistence.ResponseLoginDto;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ssg.usms.business.security.login.constant.LoginConstant.SUCCESS_LOGIN;

@Component
public class UsmsLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        UsmsUserDetails userDetails = (UsmsUserDetails) authentication.getPrincipal();
        HttpResponseUserDto responseDto = HttpResponseUserDto.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .phoneNumber(userDetails.getPhoneNumber())
                .securityState(userDetails.getSecurityState().getLevel())
                .nickname(userDetails.getPersonName())
                .email(userDetails.getEmail())
                .build();

        writeResponse(response, HttpStatus.OK.value(),SUCCESS_LOGIN,responseDto);
    }

    private void writeResponse(HttpServletResponse response, int code, String message,HttpResponseUserDto responseUserDto) throws IOException {

        ResponseLoginDto responseBody = new ResponseLoginDto(code, message, responseUserDto);

        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
