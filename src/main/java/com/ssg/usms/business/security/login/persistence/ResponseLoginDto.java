package com.ssg.usms.business.security.login.persistence;


import com.ssg.usms.business.security.login.UsmsUserDetails;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ResponseLoginDto {

    private int code;

    private String message;

    private UsmsUserDetails user;
}
