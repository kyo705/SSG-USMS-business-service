package com.ssg.usms.business.store.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum StoreState {

    READY(0),
    APPROVAL(1),
    DISAPPROVAL(2),
    STOPPED(3);

    @JsonValue
    private final int code;

    StoreState(int code) {
        this.code = code;
    }

    private static final Map<Integer, StoreState> STORE_CODE_MAP;

    static {
        STORE_CODE_MAP = new HashMap<>();
        Arrays.stream(StoreState.values()).forEach(state -> STORE_CODE_MAP.put(state.getCode(), state));
    }

    public static StoreState valueOfCode(Integer code) {
        return STORE_CODE_MAP.get(code);
    }
}
