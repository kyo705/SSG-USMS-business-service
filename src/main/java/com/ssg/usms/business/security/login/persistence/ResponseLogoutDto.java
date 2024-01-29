package com.ssg.usms.business.security.login.persistence;


import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ResponseLogoutDto {

    private int code;

    private String message;

}
