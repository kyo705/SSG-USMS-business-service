package com.ssg.usms.business.accident.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum AccidentBehavior {

    COME_IN(0),
    COME_OUT(1),
    FIGHT(2),
    STEAL(3),
    DESTROY(4),
    WASTE(5),
    DRUNKEN(6);

    private static final Map<Integer, AccidentBehavior> CODE_MAP = new HashMap<>();

    static {
        Arrays.stream(AccidentBehavior.values())
                .forEach(value -> CODE_MAP.put(value.code, value));
    }

    private final int code;

    AccidentBehavior(int code) {
        this.code = code;
    }

    public static AccidentBehavior valueOfCode(int code) {

        return CODE_MAP.get(code);
    }
}
