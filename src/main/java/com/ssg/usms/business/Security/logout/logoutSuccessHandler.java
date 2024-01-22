package com.ssg.usms.business.Security.logout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.Security.login.persistence.ResponseLoginDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class logoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if( authentication == null ){

            writeResponse(response, HttpStatus.BAD_REQUEST.value(), "세션 정보가 없습니다.");
            return;
        }

        writeResponse(response,HttpStatus.OK.value(), "로그아웃 성공");
    }

    private void writeResponse(HttpServletResponse response, int code, String message) {

        ResponseLoginDto responseBody = new ResponseLoginDto();
        responseBody.setCode(code);
        responseBody.setMessage(message);

        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
