package com.ssg.usms.business.config;


import com.ssg.usms.business.Security.login.UsmsLoginConfiguer;
import com.ssg.usms.business.Security.login.authority.UsmsAccessDeniedHandler;
import com.ssg.usms.business.Security.login.authority.UsmsForbiddenEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, AuthenticationSuccessHandler loginSuccessHandler, AuthenticationFailureHandler loginFailureHandler, LogoutSuccessHandler logoutSuccessHandler) throws Exception{

        http
                .authorizeHttpRequests((auth) -> auth
                        .antMatchers(HttpMethod.GET,("/api/users")).hasRole("ADMIN")
                        .antMatchers(HttpMethod.POST,("/api/users")).permitAll()
                        .antMatchers(HttpMethod.POST,("/api/identification")).permitAll()
                        .anyRequest().authenticated());

        http
                .csrf().disable();

        http.exceptionHandling()
                .accessDeniedHandler(new UsmsAccessDeniedHandler())
                .authenticationEntryPoint(new UsmsForbiddenEntryPoint());

        http.apply(new UsmsLoginConfiguer<>())
                .loginProcessingUrl("/api/login")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler);

        http.logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(logoutSuccessHandler);

        return http.build();
    }

}
