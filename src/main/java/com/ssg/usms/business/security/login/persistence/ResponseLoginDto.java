package com.ssg.usms.business.security.login.persistence;


import com.ssg.usms.business.security.login.UsmsUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ResponseLoginDto {

    private int code;

    private String message;

    private UsmsUserDetails user;
}
