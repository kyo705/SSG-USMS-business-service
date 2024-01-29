package com.ssg.usms.business.warning.repository;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.repository.AccidentBehaviorConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;

@ToString
@Getter
@Setter
@Entity
public class RegionWarning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "region")
    private String region;

    @Convert(converter = AccidentBehaviorConverter.class)
    @Column(name = "behavior")
    private AccidentBehavior behavior;

    @Column(name = "occurrence_count")
    private int count;

    @Column(name = "occurrence_date")
    private Date date;
}
