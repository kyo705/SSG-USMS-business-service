package com.ssg.usms.business.user.dto;


import com.ssg.usms.business.user.annotation.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HttpRequestSignUpDto {

    @UserName
    private String username;

    @Password
    private String password;

    @Email
    private String email;

    @NickName
    private String nickname;

    @PhoneNumber
    private String phoneNum;

}
