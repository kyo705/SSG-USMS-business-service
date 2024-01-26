package com.ssg.usms.business.security.logout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.security.login.persistence.ResponseLoginDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ssg.usms.business.security.login.constant.LoginConstant.NOT_SESSION;
import static com.ssg.usms.business.security.login.constant.LoginConstant.SUCCESS_LOGOUT;


@Component
public class logoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if( authentication == null ){

            writeResponse(response, HttpStatus.BAD_REQUEST.value(), NOT_SESSION);
            return;
        }

        writeResponse(response,HttpStatus.OK.value(), SUCCESS_LOGOUT);
    }

    private void writeResponse(HttpServletResponse response, int code, String message) {

        ResponseLoginDto responseBody = new ResponseLoginDto();
        responseBody.setCode(code);
        responseBody.setMessage(message);

        response.setStatus(code);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
