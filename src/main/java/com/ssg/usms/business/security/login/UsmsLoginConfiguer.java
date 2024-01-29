package com.ssg.usms.business.security.login;

import com.ssg.usms.business.device.service.DeviceService;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class UsmsLoginConfiguer <H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, UsmsLoginConfiguer<H>, UsmsAuthenticationFilter > {

    public UsmsLoginConfiguer(DeviceService deviceService){
        super (new UsmsAuthenticationFilter(deviceService),null );
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl,"POST");
    }
}
