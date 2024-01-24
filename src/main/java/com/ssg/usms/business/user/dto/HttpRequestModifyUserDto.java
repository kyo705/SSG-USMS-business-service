package com.ssg.usms.business.user.dto;


import com.ssg.usms.business.user.annotation.Email;
import com.ssg.usms.business.user.annotation.NickName;
import com.ssg.usms.business.user.annotation.Password;
import com.ssg.usms.business.user.annotation.PhoneNumber;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HttpRequestModifyUserDto {

    @Password
    private String password;

    @NickName
    private String personName;

    @PhoneNumber
    private String phoneNumber;

    @Email
    private String email;

    private String secondPassword;

    private SecurityState securityState;

}
