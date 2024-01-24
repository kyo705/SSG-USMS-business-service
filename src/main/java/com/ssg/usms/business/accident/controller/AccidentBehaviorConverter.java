package com.ssg.usms.business.accident.controller;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccidentBehaviorConverter implements Converter<String, AccidentBehavior> {

    @Override
    public AccidentBehavior convert(String source) {

        try {
            return AccidentBehavior.valueOfCode(Integer.parseInt(source));
        }
        catch (Exception e) {
            return null;
        }
    }
}
