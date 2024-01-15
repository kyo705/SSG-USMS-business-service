package com.ssg.usms.business.User.persistence;


import com.ssg.usms.business.User.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpRequestSignUpDto {

    @CustomUsername
    private String username;

    @CustomPassword
    private String password;

    @CustomEmail
    private String email;

    @CustomeNickName
    private String nickname;

    @CustomPhoneNumber
    private String phoneNum;

}
