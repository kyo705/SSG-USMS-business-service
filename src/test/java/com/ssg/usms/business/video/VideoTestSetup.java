package com.ssg.usms.business.video;

import com.ssg.usms.business.store.dto.CctvDto;
import com.ssg.usms.business.video.dto.HttpRequestCheckingStreamDto;

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

    static Stream<String> getValidFilename() {

        return Stream.of(
                UUID.randomUUID().toString().replace("-", "")
                        + "-"
                        + (System.currentTimeMillis()/1000)
                        + ".m3u8",

                UUID.randomUUID().toString().replace("-", "")
                        + "-"
                        + (System.currentTimeMillis()/1000)
                        + ".ts"
        );
    }

    static Stream<HttpRequestCheckingStreamDto> getValidCheckingDto() {

        String streamKey = UUID.randomUUID().toString().replace("-", "");

        return Stream.of(
                HttpRequestCheckingStreamDto
                        .builder()
                        .app("live")
                        .name(streamKey)
                        .addr("localhost:8080")
                        .build(),
                HttpRequestCheckingStreamDto
                        .builder()
                        .app("live")
                        .name(streamKey)
                        .addr("localhost:8080")
                        .time(System.currentTimeMillis()/1000L)
                        .build()

        );
    }
}
