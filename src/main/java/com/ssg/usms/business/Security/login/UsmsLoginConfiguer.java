package com.ssg.usms.business.Security.login;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class UsmsLoginConfiguer <H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, UsmsLoginConfiguer<H>, UsmsAuthenticationFilter > {

    public UsmsLoginConfiguer(){
        super (new UsmsAuthenticationFilter(),null );
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl,"POST");
    }
}
