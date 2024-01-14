package com.ssg.usms.business.video.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.video.dto.HttpRequestCheckingStreamDto;
import com.ssg.usms.business.video.exception.AlreadyConnectedStreamKeyException;
import com.ssg.usms.business.video.exception.ExpiredStreamKeyException;
import com.ssg.usms.business.video.exception.NotExistingStreamKeyException;
import com.ssg.usms.business.video.service.StreamKeyService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;

@ActiveProfiles("test")
@SpringBootTest
public class StreamKeyControllerCheckingAuthOfPushingStreamTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private StreamKeyService streamKeyService;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("정상적인 요청인 경우 204를 리턴한다.")
    @MethodSource("com.ssg.usms.business.video.VideoTestSetup#getValidCheckingDto")
    @ParameterizedTest
    public void testCheckAuthOfPushingStreamWithValidRequest(HttpRequestCheckingStreamDto dto) throws Exception {

        //given

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/live-streaming/checking")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("app", dto.getApp())
                                .param("name", dto.getName())
                )
                .andExpect(MockMvcResultMatchers.status().is(204))
        ;
    }

    @DisplayName("요청한 스트림 키가 만료된 경우 예외가 발생한다.")
    @MethodSource("com.ssg.usms.business.video.VideoTestSetup#getValidCheckingDto")
    @ParameterizedTest
    public void testCheckAuthOfPushingStreamWithExpiredStreamKey(HttpRequestCheckingStreamDto dto) throws Exception {

        //given
        willThrow(new ExpiredStreamKeyException())
                .given(streamKeyService)
                .checkAuthOfPushingStream(any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/live-streaming/checking")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("app", dto.getApp())
                                .param("name", dto.getName())
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(EXPIRED_STREAM_KEY);
                })
        ;
    }

    @DisplayName("요청한 스트림 키가 존재하지 않은 경우 예외가 발생한다.")
    @MethodSource("com.ssg.usms.business.video.VideoTestSetup#getValidCheckingDto")
    @ParameterizedTest
    public void testCheckAuthOfPushingStreamWithNotExistingStreamKey(HttpRequestCheckingStreamDto dto) throws Exception {

        //given
        willThrow(new NotExistingStreamKeyException())
                .given(streamKeyService)
                .checkAuthOfPushingStream(any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/live-streaming/checking")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("app", dto.getApp())
                                .param("name", dto.getName())
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_STREAM_KEY);
                })
        ;
    }

    @DisplayName("요청한 스트림 키가 이미 사용 중이라면 예외가 발생한다.")
    @Test
    public void testCheckAuthOfPushingStreamWithNotExistingStreamKey() throws Exception {

        //given
        HttpRequestCheckingStreamDto dto = HttpRequestCheckingStreamDto
                .builder()
                .app("live")
                .name("streamKey")
                .addr("localhost:8080")
                .build();

        willThrow(new AlreadyConnectedStreamKeyException())
                .given(streamKeyService)
                .checkAuthOfPushingStream(any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/live-streaming/checking")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("app", dto.getApp())
                                .param("name", dto.getName())
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(ALREADY_CONNECTED_STREAM_KEY);
                })
        ;
    }
}
