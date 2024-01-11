package com.ssg.usms.business.error;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {

    private int code;
    private String message;
}
