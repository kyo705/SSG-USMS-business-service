package com.ssg.usms.business.accident.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.dto.HttpRequestCreatingAccidentDto;
import com.ssg.usms.business.accident.service.AccidentService;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.service.StoreService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.UUID;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest
public class AccidentControllerCreatingTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private StoreService storeService;
    @MockBean
    private AccidentService accidentService;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("[createAccident] : 정상적인 파라미터로 요청시 이상 행동이 기록된다.")
    @Test
    public void testCreateAccidentWithValidParam() throws Exception {

        //given
        HttpRequestCreatingAccidentDto requestBody = new HttpRequestCreatingAccidentDto();
        requestBody.setStreamKey(UUID.randomUUID().toString());
        requestBody.setBehavior(AccidentBehavior.COME_IN);
        requestBody.setStartTimestamp(System.currentTimeMillis());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/live-streaming/accidents")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()));


    }

    @DisplayName("[createAccident] : 잘못된 스트림키 양식으로 요청시 예외가 발생한다.")
    @Test
    public void testCreateAccidentWithInvalidStreamKey() throws Exception {

        //given
        HttpRequestCreatingAccidentDto requestBody = new HttpRequestCreatingAccidentDto();
        requestBody.setStreamKey("올바르지 못한 uuid 형태");
        requestBody.setBehavior(AccidentBehavior.COME_IN);
        requestBody.setStartTimestamp(System.currentTimeMillis());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/live-streaming/accidents")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(
                                                            result.getResponse().getContentAsString(UTF_8),
                                                            ErrorResponseDto.class
                                                    );

                    Assertions.assertThat(responseBody.getCode()).isEqualTo(INVALID_STREAM_KEY_FORMAT_CODE);
                })
        ;


    }

    @DisplayName("[createAccident] : 잘못된 이상 행동 양식으로 요청시 예외가 발생한다.")
    @ValueSource(strings = {"7", "10", "-1", "잘못된 값"})
    @ParameterizedTest
    public void testCreateAccidentWithInvalidBehavior(String invalidBehavior) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/live-streaming/accidents")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"streamKey\" : \"" + UUID.randomUUID() + "\", " +
                                        "\"behavior\" : " + invalidBehavior + ", " +
                                        "\"startTimestamp\" : " + System.currentTimeMillis() +
                                        "}")

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(
                            result.getResponse().getContentAsString(UTF_8),
                            ErrorResponseDto.class
                    );

                    Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
                })
        ;


    }

    @DisplayName("[createAccident] : 잘못된 이상 행동 양식으로 요청시 예외가 발생한다.")
    @Test
    public void testCreateAccidentWithInvalidBehavior2() throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/live-streaming/accidents")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"streamKey\" : \"" + UUID.randomUUID() + "\", " +
                                        "\"startTimestamp\" : " + System.currentTimeMillis() +
                                        "}")

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(
                            result.getResponse().getContentAsString(UTF_8),
                            ErrorResponseDto.class
                    );

                    Assertions.assertThat(responseBody.getCode()).isEqualTo(NOT_EXISTING_ACCIDENT_BEHAVIOR_CODE);
                })
        ;


    }

    @DisplayName("[createAccident] : 잘못된 타입스탬프 양식으로 요청시 예외가 발생한다.")
    @NullSource
    @ValueSource(strings = {"0", "-159301"})
    @ParameterizedTest
    public void testCreateAccidentWithInvalidTimestamp(String invalidTimestamp) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/live-streaming/accidents")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"streamKey\" : \"" + UUID.randomUUID() + "\", " +
                                        "\"behavior\" : 5, " +
                                        "\"startTimestamp\" : " + invalidTimestamp +
                                        "}")

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(
                            result.getResponse().getContentAsString(UTF_8),
                            ErrorResponseDto.class
                    );

                    Assertions.assertThat(responseBody.getCode()).isEqualTo(INVALID_TIMESTAMP_FORMAT_CODE);
                })
        ;


    }

    @DisplayName("[createAccident] : 타임스탬프 값 없이 요청시 예외가 발생한다.")
    @ValueSource(strings = {"잘못된 양식", "-159301#"})
    @ParameterizedTest
    public void testCreateAccidentWithInvalidTimestamp2(String invalidTimestamp) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/live-streaming/accidents")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"streamKey\" : \"" + UUID.randomUUID() + "\", " +
                                        "\"behavior\" : 5, " +
                                        "\"startTimestamp\" : " + invalidTimestamp +
                                        "}")

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(
                            result.getResponse().getContentAsString(UTF_8),
                            ErrorResponseDto.class
                    );

                    Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
                })
        ;


    }
}
