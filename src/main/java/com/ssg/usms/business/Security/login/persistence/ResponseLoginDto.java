package com.ssg.usms.business.Security.login.persistence;


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
}
