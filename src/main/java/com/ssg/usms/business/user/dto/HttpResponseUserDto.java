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

    private String personName;

    private String phoneNumber;

    private String email;

    private SecurityState securityState;

}
