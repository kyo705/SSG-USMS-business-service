package com.ssg.usms.business.video;

import com.ssg.usms.business.store.CctvDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class VideoTestSetup {

    public static final String USERNAME = "kyo705";
    static Stream<String> getInvalidFormOfStreamKey() {

        return Stream.of(
                UUID.randomUUID().toString(),                   // 36 자리 수 (반드시 32자리어야함)
                UUID.randomUUID().toString().substring(0, 32)   // 특수 문자 포함 (특수 문자가 포함되어선 안됨)
        );
    }

    static Stream<List<CctvDto>> getNotExistingCctvDto() {

        return Stream.of(
                List.of()
        );
    }
}
