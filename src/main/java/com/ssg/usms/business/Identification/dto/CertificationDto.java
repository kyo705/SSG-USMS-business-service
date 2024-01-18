package com.ssg.usms.business.Identification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@Builder
@Getter
public class CertificationDto {

    private String key;

    private String value;

    @Override
    public String toString(){
        return this.key +" "+this.value;
    }
}
