package com.ssg.usms.business.config;


import com.ssg.usms.business.security.login.UsmsLoginConfiguer;
import com.ssg.usms.business.security.login.authority.UsmsAccessDeniedHandler;
import com.ssg.usms.business.security.login.authority.UsmsForbiddenEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static com.ssg.usms.business.user.dto.UserRole.ROLE_ADMIN;
import static com.ssg.usms.business.user.dto.UserRole.ROLE_STORE_OWNER;

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
                        .antMatchers(HttpMethod.GET,("/api/users")).hasRole(ROLE_ADMIN.getRole())
                        .antMatchers(HttpMethod.POST,("/api/users")).permitAll()
                        .antMatchers(HttpMethod.POST,("/api/identification")).permitAll()

                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores")).hasAnyRole(ROLE_ADMIN.getRole(), ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}")).hasAnyRole(ROLE_ADMIN.getRole(), ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}/license/{licenseKey}")).hasAnyRole(ROLE_ADMIN.getRole(), ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.POST,("/api/users/{userId}/stores")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.POST,("/api/users/{userId}/stores/{storeId}")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.PATCH,("/api/users/{userId}/stores/{storeId}")).hasRole(ROLE_ADMIN.getRole())
                        .antMatchers(HttpMethod.DELETE,("/api/users/{userId}/stores/{storeId}")).hasRole(ROLE_STORE_OWNER.getRole())

                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}/cctvs/")).hasAnyRole(ROLE_ADMIN.getRole(), ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}")).hasAnyRole(ROLE_ADMIN.getRole(), ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.POST,("/api/users/{userId}/stores/{storeId}/cctvs")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.PATCH,("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.DELETE,("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}")).hasRole(ROLE_STORE_OWNER.getRole())

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
