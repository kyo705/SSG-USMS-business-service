package com.ssg.usms.business.accident.dto;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccidentStatDto {

    private Long storeId;
    private AccidentBehavior behavior;
    private Long count;
    private String startDate;
    private String endDate;

    public AccidentStatDto(Long storeId, AccidentBehavior behavior, Long count, String startDate, String endDate) {
        this.storeId = storeId;
        this.behavior = behavior;
        this.count = count;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
