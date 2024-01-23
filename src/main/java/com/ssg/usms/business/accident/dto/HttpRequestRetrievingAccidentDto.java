package com.ssg.usms.business.accident.dto;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HttpRequestRetrievingAccidentDto {

    private List<AccidentBehavior> behavior;
    private String startDate;
    private String endDate;
    private int offset;
    private int size;
}
