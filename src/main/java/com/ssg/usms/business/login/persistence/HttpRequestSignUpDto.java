package com.ssg.usms.business.login.persistence;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager;

import javax.validation.constraints.Email;

@Getter
@Setter
public class HttpRequestSignUpDto {

    private String username;

    private String password;
    @Email
    private String email;

    private String nickname;

    private String phoneNum;

}
