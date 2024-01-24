package com.ssg.usms.business.Security.login.persistence;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestLoginDto {

    private String username;

    private String password;
}
