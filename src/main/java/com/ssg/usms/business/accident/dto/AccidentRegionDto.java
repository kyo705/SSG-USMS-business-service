package com.ssg.usms.business.accident.dto;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import lombok.*;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccidentRegionDto {

    private Long accidentId;
    private String storeAddress;
    private AccidentBehavior behavior;
}
