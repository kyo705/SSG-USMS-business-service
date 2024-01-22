package com.ssg.usms.business.store.repository;

import com.ssg.usms.business.store.constant.StoreState;

import javax.persistence.AttributeConverter;

public class StoreStateConverter implements AttributeConverter<StoreState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(StoreState attribute) {
        return attribute.getCode();
    }

    @Override
    public StoreState convertToEntityAttribute(Integer dbData) {
        return StoreState.valueOfCode(dbData);
    }
}
