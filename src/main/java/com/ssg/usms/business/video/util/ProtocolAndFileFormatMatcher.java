package com.ssg.usms.business.video.util;

import com.ssg.usms.business.video.exception.NotAllowedStreamingProtocolException;
import com.ssg.usms.business.video.exception.NotMatchingStreamingProtocolAndFileFormatException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProtocolAndFileFormatMatcher {

    private static final Map<String, List<String>> PROTOCOL_AND_FILE_FORMAT_MAP;

    static  {

        PROTOCOL_AND_FILE_FORMAT_MAP = new HashMap<>();
        PROTOCOL_AND_FILE_FORMAT_MAP.put("hls", List.of("m3u8", "ts"));
    }

    public static boolean matches(String key, String value) {

        List<String> values = PROTOCOL_AND_FILE_FORMAT_MAP.get(key);
        if(values == null) {
            throw  new NotAllowedStreamingProtocolException("허용되지 않은 스트리밍 프로토콜입니다.");
        }
        if(!values.contains(value)) {
            throw new NotMatchingStreamingProtocolAndFileFormatException("스트리밍 프로토콜과 파일 확장자명이 매칭되지 않습니다.");
        }
        return true;
    }
}
