package com.ssg.usms.business.Identification.dto;


import com.ssg.usms.business.Identification.annotation.Code;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class HttpRequestIdentificationDto {

    @Code
    private int code;

    private String value;

}
