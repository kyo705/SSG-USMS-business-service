package com.ssg.usms.business.config;


import com.ssg.usms.business.device.service.DeviceService;
import com.ssg.usms.business.security.login.UsmsLoginConfiguer;
import com.ssg.usms.business.security.login.authority.UsmsAccessDeniedHandler;
import com.ssg.usms.business.security.login.authority.UsmsForbiddenEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.ssg.usms.business.user.dto.UserRole.ROLE_ADMIN;
import static com.ssg.usms.business.user.dto.UserRole.ROLE_STORE_OWNER;

@Configuration
public class SecurityConfig {

    @Value("${usms.front-server.url}")
    private String frontEndServerUrl;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, AuthenticationSuccessHandler loginSuccessHandler,
                                                          AuthenticationFailureHandler loginFailureHandler,
                                                          LogoutSuccessHandler logoutSuccessHandler,
                                                          DeviceService deviceService) throws Exception{

        http
                .authorizeHttpRequests((auth) -> auth
                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}/replay")).hasRole(ROLE_STORE_OWNER.getRole())

                        .antMatchers(HttpMethod.GET,("/video/{protocol}/live/{streamKey}/{filename}")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.GET,("/video/{protocol}/replay/{streamKey}/{filename}")).hasRole(ROLE_STORE_OWNER.getRole())

                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}/cctvs/accidents")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}/cctvs/accidents/stats")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}/accidents/region")).hasRole(ROLE_STORE_OWNER.getRole())

                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}/cctvs")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.POST,("/api/users/{userId}/stores/{storeId}/cctvs")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.PATCH,("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.DELETE,("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}")).hasRole(ROLE_STORE_OWNER.getRole())

                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores")).hasAnyRole(ROLE_ADMIN.getRole(), ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}")).hasAnyRole(ROLE_ADMIN.getRole(), ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.GET,("/api/users/{userId}/stores/{storeId}/license/{licenseKey}")).hasAnyRole(ROLE_ADMIN.getRole(), ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.POST,("/api/users/{userId}/stores")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.POST,("/api/users/{userId}/stores/{storeId}")).hasRole(ROLE_STORE_OWNER.getRole())
                        .antMatchers(HttpMethod.PATCH,("/api/users/{userId}/stores/{storeId}")).hasRole(ROLE_ADMIN.getRole())
                        .antMatchers(HttpMethod.DELETE,("/api/users/{userId}/stores/{storeId}")).hasRole(ROLE_STORE_OWNER.getRole())

                        .antMatchers(HttpMethod.GET,("/api/users")).hasRole(ROLE_ADMIN.getRole())
                        .antMatchers(HttpMethod.POST,("/api/users")).permitAll()
                        .antMatchers(HttpMethod.POST,("/api/identification")).permitAll()

                        .anyRequest().permitAll());

        http
                .csrf().disable();

        http.cors();

        http.exceptionHandling()
                .accessDeniedHandler(new UsmsAccessDeniedHandler())
                .authenticationEntryPoint(new UsmsForbiddenEntryPoint());

        http.apply(new UsmsLoginConfiguer<>(deviceService))
                .loginProcessingUrl("/api/login")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler);

        http.logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(logoutSuccessHandler);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontEndServerUrl));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
