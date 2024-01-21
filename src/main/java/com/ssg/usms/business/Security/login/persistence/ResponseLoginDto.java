package com.ssg.usms.business.Security.login.persistence;


import com.ssg.usms.business.Security.login.UsmsUserDetails;
import com.ssg.usms.business.user.repository.UsmsUser;
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
