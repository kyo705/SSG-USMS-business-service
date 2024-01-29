package com.ssg.usms.business.user.dto;


import com.ssg.usms.business.user.annotation.Email;
import com.ssg.usms.business.user.annotation.NickName;
import com.ssg.usms.business.user.annotation.Password;
import com.ssg.usms.business.user.annotation.PhoneNumber;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HttpRequestModifyUserDto {

    @Password
    private String password;

    @NickName
    private String nickname;

    @PhoneNumber
    private String phoneNumber;

    @Email
    private String email;

    private String secondPassword;

//    @SecurityStateValid
    private SecurityState securityState;

}
