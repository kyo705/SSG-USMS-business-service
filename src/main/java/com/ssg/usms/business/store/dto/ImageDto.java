package com.ssg.usms.business.store.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {

    private String contentType;
    private long contentLength;
    private byte[] content;
}
