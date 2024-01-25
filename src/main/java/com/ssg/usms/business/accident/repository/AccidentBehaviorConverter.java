package com.ssg.usms.business.accident.repository;

import com.ssg.usms.business.accident.constant.AccidentBehavior;

import javax.persistence.AttributeConverter;

public class AccidentBehaviorConverter implements AttributeConverter<AccidentBehavior, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AccidentBehavior attribute) {

        return attribute.getCode();
    }

    @Override
    public AccidentBehavior convertToEntityAttribute(Integer code) {

        return AccidentBehavior.valueOfCode(code);
    }
}
