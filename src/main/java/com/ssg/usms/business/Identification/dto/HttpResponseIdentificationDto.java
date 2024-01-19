package com.ssg.usms.business.Identification.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpResponseIdentificationDto {

    private int code;

    private String message;
}
