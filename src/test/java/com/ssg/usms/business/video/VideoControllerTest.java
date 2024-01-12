package com.ssg.usms.business.video;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.video.exception.ExpiredStreamKeyException;
import com.ssg.usms.business.video.exception.NotExistingStreamKeyException;
import com.ssg.usms.business.video.exception.NotOwnedStreamKeyException;
import com.ssg.usms.business.video.service.VideoService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static org.mockito.BDDMockito.given;



@ActiveProfiles("test")
@SpringBootTest
public class VideoControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private VideoService videoService;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    // 1. 라이브 스트리밍 비디오 요청

    @DisplayName("라이브 스트리밍 비디오 요청 : 유저가 자기 자신의 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 실제 파일 경로를 리턴한다.")
    @ValueSource(strings = {"test.m3u8", "135.ts", "time-13513.ts"})
    @ParameterizedTest
    public void testGetLiveVideoWithValidParam(String filename) throws Exception {

        //given
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "hls";

        given(videoService.getLiveVideo(streamKey))
                .willReturn(String.format("localhost:8090/video/%s/live/%s/%s", streamKey, protocol, filename));

        //when & then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/video/{streamKey}/live/{protocol}/{filename}", streamKey, protocol, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(307))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, String.format("localhost:8090/video/%s/live/%s/%s", streamKey, protocol, filename)))
        ;

    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 만료된 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithExpiredStreamKey() throws Exception {

        //given
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "hls";
        String filename = "test.m3u8";

        given(videoService.getLiveVideo(streamKey))
                .willThrow(new ExpiredStreamKeyException("유효하지 않은 스트림 키 값입니다."));

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{streamKey}/live/{protocol}/{filename}", streamKey, protocol, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(EXPIRED_STREAM_KEY);
                })
        ;
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 존재하지 않는 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithNotExistingStreamKey() throws Exception {

        //given
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "hls";
        String filename = "test.m3u8";

        given(videoService.getLiveVideo(streamKey))
                .willThrow(new NotExistingStreamKeyException("유효하지 않은 스트림 키 값입니다."));

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{streamKey}/live/{protocol}/{filename}", streamKey, protocol, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_STREAM_KEY);
                })
        ;
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 타인의 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithNotOwnedStreamKey() throws Exception {

        //given
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "hls";
        String filename = "test.m3u8";

        given(videoService.getLiveVideo(streamKey))
                .willThrow(new NotOwnedStreamKeyException("유효하지 않은 스트림 키 값입니다."));

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{streamKey}/live/{protocol}/{filename}", streamKey, protocol, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_STREAM_KEY);
                })
        ;
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 허용되지 않은 스트림 프토로콜 타입으로 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithNotAllowedStreamingProtocol() throws Exception {

        //given
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "ftp";
        String filename = "test.mp4";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{streamKey}/live/{protocol}/{filename}", streamKey, protocol, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_STREAM_PROTOCOL);
                })
        ;

    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 스트림 프토로콜 타입과 불일치하는 파일 확장자로 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithNotMatchingProtocolAndFileFormat() throws Exception {

        //given
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "hls";
        String filename = "test.mp4";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{streamKey}/live/{protocol}/{filename}", streamKey, protocol, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_MATCHING_STREAM_PROTOCOL_AND_FILE_FORMAT);
                })
        ;

    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 요청 스트림 키가 허용된 규격(32자리 수, 특수 문자 포함 X)에 벗어난 경우 예외를 발생시킨다.")
    @MethodSource("com.ssg.usms.business.video.VideoTestSetup#getInvalidFormOfStreamKey")
    @ParameterizedTest
    public void testGetLiveVideoWithNotAllowedFormOfStreamKey(String streamKey) throws Exception {
        
        //given
        String protocol = "hls";
        String filename = "test.m3u8";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{streamKey}/live/{protocol}/{filename}", streamKey, protocol, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
        ;

    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 요청 파일 명이 허용된 규격('파일 이름'.'확장자 명')에 벗어난 경우 예외를 발생시킨다.")
    @ValueSource(strings = {"test!@!.m3u8", "TEST.TT.m3u8", "test1"})
    @ParameterizedTest
    public void testGetLiveVideoWithNotAllowedFormOfFilename(String filename) throws Exception {

        //given
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "hls";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{streamKey}/live/{protocol}/{filename}", streamKey, protocol, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
        ;

    }
}
