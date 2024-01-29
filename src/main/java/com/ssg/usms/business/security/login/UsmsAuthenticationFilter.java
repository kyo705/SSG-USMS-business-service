package com.ssg.usms.business.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.security.login.persistence.RequestLoginDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;




@Slf4j
public class UsmsAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,HttpServletResponse response ){

        if(!request.getMethod().equals(HttpMethod.POST.name())){
            throw new AuthenticationServiceException("Authentication not supported :" + request.getMethod());
        }

        RequestLoginDto dto = parseRequestLoginDto(request);

        if( dto.getToken() == null){
            throw new AuthenticationServiceException("No Token");
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

        setDetails(request, usernamePasswordAuthenticationToken);
        usernamePasswordAuthenticationToken.setDetails(dto);

        return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }

    private RequestLoginDto parseRequestLoginDto(HttpServletRequest request){

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader br = request.getReader();
            String str;

            while((str = br.readLine()) != null){
                stringBuilder.append(str);
            }
            return objectMapper.readValue(stringBuilder.toString(), RequestLoginDto.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
