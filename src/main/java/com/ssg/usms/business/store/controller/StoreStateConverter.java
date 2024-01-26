package com.ssg.usms.business.store.controller;

import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.video.exception.NotExistingStreamKeyException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StoreStateConverter implements Converter<String, StoreState> {

    @Override
    public StoreState convert(String code) {

        try {
            return StoreState.valueOfCode(Integer.parseInt(code));
        }
        catch (Exception e) {
            throw new NotExistingStreamKeyException();
        }
    }
}
