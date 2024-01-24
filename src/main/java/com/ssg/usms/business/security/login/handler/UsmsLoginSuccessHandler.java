package com.ssg.usms.business.security.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.security.login.persistence.ResponseLoginDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UsmsLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        UsmsUserDetails userDetails = (UsmsUserDetails) authentication.getPrincipal();

        writeResponse(response, HttpStatus.OK.value(),"로그인 성공",userDetails);
    }

    private void writeResponse(HttpServletResponse response, int code, String message,UsmsUserDetails userDetails) throws IOException {

        ResponseLoginDto responseBody = new ResponseLoginDto(code, message, userDetails);

        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
