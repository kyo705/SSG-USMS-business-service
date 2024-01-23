package com.ssg.usms.business.accident.repository;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Accident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cctvId;

    @Convert(converter = AccidentBehaviorConverter.class)
    private AccidentBehavior behavior;

    private Long startTimestamp;

    public static Accident init(Long cctvId, AccidentBehavior behavior, Long timestamp) {

        Accident accident = new Accident();
        accident.setCctvId(cctvId);
        accident.setBehavior(behavior);
        accident.setStartTimestamp(timestamp);

        return accident;
    }
}
