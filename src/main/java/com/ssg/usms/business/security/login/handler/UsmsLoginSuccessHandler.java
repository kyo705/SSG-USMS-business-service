package com.ssg.usms.business.security.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.device.service.DeviceService;
import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.security.login.persistence.RequestLoginDto;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@RequiredArgsConstructor
public class UsmsLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DeviceService deviceService;

    @Override
    @Transactional
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

        RequestLoginDto dto = (RequestLoginDto) authentication.getDetails();

        deviceService.saveToken(dto.getToken(), userDetails.getId());

        writeResponse(response, HttpStatus.OK.value(),responseDto);
    }

    private void writeResponse(HttpServletResponse response, int code,HttpResponseUserDto responseUserDto) throws IOException {

        response.setStatus(code);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(responseUserDto));
    }
}
