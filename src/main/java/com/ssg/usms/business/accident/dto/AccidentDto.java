package com.ssg.usms.business.accident.dto;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.repository.Accident;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccidentDto {

    private Long id;
    private Long cctvId;
    private AccidentBehavior behavior;
    private long startTimestamp;

    public AccidentDto(Accident accident) {

        this.id = accident.getId();
        this.cctvId = accident.getCctvId();
        this.behavior = accident.getBehavior();
        this.startTimestamp = accident.getStartTimestamp();
    }

}
