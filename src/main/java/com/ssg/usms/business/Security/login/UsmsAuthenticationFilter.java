package com.ssg.usms.business.Security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.Security.login.persistence.RequestLoginDto;
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
    public Authentication attemptAuthentication(HttpServletRequest req,HttpServletResponse res ){

        if(!req.getMethod().equals(HttpMethod.POST.name())){
            throw new AuthenticationServiceException("Authentication not supported :" + req.getMethod());
        }

        RequestLoginDto dto = parseRequestLoginDto(req);
        String username = dto.getUsername();
        String password = dto.getPassword();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        setDetails(req, usernamePasswordAuthenticationToken);

        Authentication authentication = getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);

        return authentication;
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
