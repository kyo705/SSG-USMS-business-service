package com.ssg.usms.business.video.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.exception.UnavailableStoreException;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@WithUserDetails("storeOwner")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT,classes = EmbeddedRedis.class)
public class VideoControllerReplayStreamTest {

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
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
    // 2. 리플레이 스트리밍 비디오 요청

    @DisplayName("리플레이 스트리밍 비디오 요청 : 유저가 자기 자신의 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 실제 파일 데이터를 리턴한다.")
    @MethodSource("com.ssg.usms.business.video.VideoTestSetup#getValidFilename")
    @ParameterizedTest
    public void testGetReplayVideoWithValidParam(String filename) throws Exception {

        //given
        Long userId = 1L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";
        byte[] streamData = new byte[50];

        given(videoService.getReplayVideo(userId, streamKey, filename)).willReturn(streamData);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{protocol}/replay/{streamKey}/{filename}", protocol, streamKey, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM))
        ;

    }

    @DisplayName("리플레이 스트리밍 비디오 요청 : 만료된 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetReplayVideoWithExpiredStreamKey() throws Exception {

        //given
        Long userId = 1L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";
        String filename = UUID.randomUUID() + "-" + (System.currentTimeMillis()/1000) + ".m3u8";

        given(videoService.getReplayVideo(userId, streamKey, filename))
                .willThrow(new ExpiredStreamKeyException());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{protocol}/replay/{streamKey}/{filename}", protocol, streamKey, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(EXPIRED_STREAM_KEY);
                })
        ;
    }

    @DisplayName("리플레이 스트리밍 비디오 요청 : 존재하지 않는 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetReplayVideoWithNotExistingStreamKey() throws Exception {

        //given
        Long userId = 1L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";
        String filename = UUID.randomUUID() + "-" + (System.currentTimeMillis()/1000) + ".m3u8";


        given(videoService.getReplayVideo(userId, streamKey, filename))
                .willThrow(new NotExistingStreamKeyException());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{protocol}/replay/{streamKey}/{filename}", protocol, streamKey, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_STREAM_KEY);
                })
        ;
    }

    @DisplayName("리플레이 스트리밍 비디오 요청 : 타인의 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetReplayVideoWithNotOwnedStreamKey() throws Exception {

        //given
        Long userId = 1L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";
        String filename = UUID.randomUUID() + "-" + (System.currentTimeMillis()/1000) + ".m3u8";

        given(videoService.getReplayVideo(userId, streamKey, filename))
                .willThrow(new NotOwnedStreamKeyException());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{protocol}/replay/{streamKey}/{filename}", protocol, streamKey, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_STREAM_KEY);
                })
        ;
    }

    @DisplayName("리플레이 스트리밍 비디오 요청 : 허용되지 않은 스트림 프토로콜 타입으로 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetReplayVideoWithNotAllowedStreamingProtocol() throws Exception {

        //given
        Long userId = 1L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "ftp";
        String filename = UUID.randomUUID() + "-" + (System.currentTimeMillis()/1000) + ".m3u8";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{protocol}/replay/{streamKey}/{filename}", protocol, streamKey, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_STREAM_PROTOCOL);
                })
        ;

    }

    @DisplayName("리플레이 스트리밍 비디오 요청 : 스트림 프토로콜 타입과 불일치하는 파일 확장자로 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetReplayVideoWithNotMatchingProtocolAndFileFormat() throws Exception {

        //given
        Long userId = 1L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";
        String filename = UUID.randomUUID()+ "-" + (System.currentTimeMillis()/1000) + ".mp4";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{protocol}/replay/{streamKey}/{filename}", protocol, streamKey, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_MATCHING_STREAM_PROTOCOL_AND_FILE_FORMAT);
                })
        ;

    }

    @DisplayName("리플레이 스트리밍 비디오 요청 : 요청한 스트림 키가 이용 불가능한 매장인 경우 예외를 발생시킨다.")
    @Test
    public void testGetReplayVideoWithUnavailableStore() throws Exception {

        //given
        Long userId = 1L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";
        String filename = UUID.randomUUID()+ "-" + (System.currentTimeMillis()/1000) + ".m3u8";

        given(videoService.getReplayVideo(userId, streamKey, filename)).willThrow(new UnavailableStoreException());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{protocol}/replay/{streamKey}/{filename}", protocol, streamKey, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(UNAVAILABLE_STORE_CODE);
                })
        ;

    }

    @DisplayName("리플레이 스트리밍 비디오 요청 : 요청 스트림 키가 허용된 규격(32자리 수, 특수 문자 포함 X)에 벗어난 경우 예외를 발생시킨다.")
    @MethodSource("com.ssg.usms.business.video.VideoTestSetup#getInvalidFormOfStreamKey")
    @ParameterizedTest
    public void testGetReplayVideoWithNotAllowedFormOfStreamKey(String streamKey) throws Exception {

        //given
        String protocol = "hls";
        String filename = UUID.randomUUID() + "-" + (System.currentTimeMillis()/1000) + ".m3u8";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{protocol}/replay/{streamKey}/{filename}", protocol, streamKey, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
        ;

    }

    @DisplayName("리플레이 스트리밍 비디오 요청 : 요청 파일 명이 허용된 규격('파일 이름'.'확장자 명')에 벗어난 경우 예외를 발생시킨다.")
    @ValueSource(strings = {"test!@!.m3u8", "TEST.TT.m3u8", "test1"})
    @ParameterizedTest
    public void testGetReplayVideoWithNotAllowedFormOfFilename(String filename) throws Exception {

        //given
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/video/{protocol}/replay/{streamKey}/{filename}", protocol, streamKey, filename)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
        ;

    }
}
