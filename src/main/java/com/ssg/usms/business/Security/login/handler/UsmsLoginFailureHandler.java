package com.ssg.usms.business.Security.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.Security.login.persistence.ResponseLoginDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class UsmsLoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String failureMessage = exception.getMessage();
        log.error(failureMessage);

        writeResponse(response, HttpStatus.BAD_REQUEST.value(),failureMessage);
    }

    private void writeResponse(HttpServletResponse response, int code, String message) throws IOException {

        ResponseLoginDto responseBody = new ResponseLoginDto(code, message, null);

        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
