package com.ssg.usms.business.video.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequestCheckingStreamDto {

    private String app;     /* application name */
    private String name;    /* stream name(stream key) */
    private String addr;    /* client IP address */
    private long time;      /* the number of seconds since publish call */
}
