package com.ssg.usms.business.user.dto;


import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class HttpResponseUserDto {


    private Long id;

    private String username;

    private String nickname;

    private String phoneNumber;

    private String email;

    private int securityState;

}
