//package com.ssg.usms.business.identification.controller;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ssg.usms.business.Identification.Service.IdentificationService;
//import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
//import com.ssg.usms.business.Identification.dto.HttpResponseIdentificationDto;
//import com.ssg.usms.business.Identification.dto.SmsCertificationDao;
//import com.ssg.usms.business.Identification.error.NotIdentificationException;
//import com.ssg.usms.business.config.EmbeddedRedis;
//import com.ssg.usms.business.error.ErrorResponseDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.filter.CharacterEncodingFilter;
//
//import javax.persistence.Embeddable;
//
//import static com.ssg.usms.business.Identification.constant.IdenticationConstant.INVALID_AUTHENTICATION_CODE_LITERAL;
//import static com.ssg.usms.business.constant.CustomStatusCode.*;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//
//@ActiveProfiles("test")
//@SpringBootTest(classes = EmbeddedRedis.class)
//public class IdentificationControllerTest {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Autowired
//    private WebApplicationContext context;
//    private MockMvc mockMvc;
//    @MockBean
//    private IdentificationService service;
//
//    @BeforeEach
//    public void setup() {
//
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .addFilters(new CharacterEncodingFilter("UTF-8", true))
//                .build();
//    }
//
//    @DisplayName("인증번호 발송이 성공한 경우 x-authenticate-key 헤더와 함께 200리턴")
//    @Test
//    public void testSuccessSendVerificationCode() throws Exception {
//
//        HttpRequestIdentificationDto requestdto = HttpRequestIdentificationDto.builder()
//                .code(1)
//                .value("010-4046-7715")
//                .build();
//
//        doNothing().when(service).createIdentification(any(),any());
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.post("/api/identification")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(requestdto))
//                )
//                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
//                .andExpect(MockMvcResultMatchers.header().exists("x-authenticate-key"))
//                .andExpect(result -> {
//                    HttpResponseIdentificationDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), HttpResponseIdentificationDto.class);
//                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.OK.value());
//                });
//    }
//
//    @DisplayName("존재하지 않는 요청 코드인 경우 BAD REQUEST와 함께 605 코드 리턴")
//    @Test
//    public void testNotAllowedCodeForm() throws Exception {
//
//        HttpRequestIdentificationDto requestdto = HttpRequestIdentificationDto.builder()
//                .code(3)
//                .value("010-4046-7715")
//                .build();
//
//        doNothing().when(service).createIdentification(any(),any());
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.post("/api/identification")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(requestdto))
//                )
//                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
//                .andExpect(result -> {
//                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);
//                    assertThat(responseBody.getCode()).isEqualTo(NOT_ALLOWED_CODE_FORM);
//                });
//    }
//
//
//
//    @DisplayName("code가 0인경우에 부적절한 value 양식일경우 BAD REQUEST , 606 리턴")
//    @ValueSource(strings = {"010-4143-4132", "123181321"})
//    @ParameterizedTest
//    public void testNotMatchedCodeAndValueEmail(String values) throws Exception {
//
//        HttpRequestIdentificationDto requestdto = HttpRequestIdentificationDto.builder()
//                .code(0)
//                .value(values)
//                .build();
//
//        doNothing().when(service).createIdentification(any(),any());
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.post("/api/identification")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(requestdto))
//                )
//                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
//                .andExpect(result -> {
//                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);
//                    assertThat(responseBody.getCode()).isEqualTo(NOT_MATCHED_CODE_VALUE);
//                });
//    }
//
//    @DisplayName("code가 1인경우에 부적절한 value 양식일경우 BAD REQUEST , 606 리턴")
//    @ValueSource(strings = {"011-1231-1234", "ans31232@naver.com"})
//    @ParameterizedTest
//    public void testNotMatchedCodeAndValueSms(String values) throws Exception {
//
//        HttpRequestIdentificationDto requestdto = HttpRequestIdentificationDto.builder()
//                .code(1)
//                .value(values)
//                .build();
//
//        doNothing().when(service).createIdentification(any(),any());
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.post("/api/identification")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(requestdto))
//                )
//                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
//                .andExpect(result -> {
//                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);
//                    assertThat(responseBody.getCode()).isEqualTo(NOT_MATCHED_CODE_VALUE);
//                });
//    }
//
//    @DisplayName("인증 번호 발송 중 예외가 발생한 경우 BAD GATEWAY , 701 상태코드 리턴")
//    @Test
//    public void testBadGatewayException() throws Exception {
//
//        HttpRequestIdentificationDto requestdto = HttpRequestIdentificationDto.builder()
//                .code(1)
//                .value("010-4046-7715")
//                .build();
//
//        doThrow(new RuntimeException()).when(service).createIdentification(any(),any());
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.post("/api/identification")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(requestdto))
//                )
//                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_GATEWAY.value()))
//                .andExpect(result -> {
//                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);
//                    assertThat(responseBody.getCode()).isEqualTo(BAD_GATEWAY_IN_IDENTIFICATION);
//                });
//    }
//
//    @DisplayName("인증번호로 본인인증을 시도했는데 성공한경우 200 상태코드,jwt 토큰 발급")
//    @Test
//    public void testSuccessVerify() throws Exception {
//
//        HttpRequestIdentificationDto requestdto = HttpRequestIdentificationDto.builder()
//                .code(1)
//                .value("010-4046-7715")
//                .build();
//
//        given(service.verifyIdentification(any())).willReturn(requestdto);
//
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.get("/api/identification")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .header("x-authenticate-key","uuid")
//                                .param("identificationCode","12345")
//                )
//                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
//                .andExpect(result -> {
//                    HttpResponseIdentificationDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), HttpResponseIdentificationDto.class);
//                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.OK.value());
//                    assertThat(result.getResponse().getHeader("Authorization")).isNotNull();
//                });
//    }
//
//    @DisplayName("인증번호로 본인인증을 시도했는데 실패한경우 400 상태코드 리턴")
//    @Test
//    public void testFailedVerify() throws Exception {
//
//        given(service.verifyIdentification(any())).willThrow(new NotIdentificationException(INVALID_AUTHENTICATION_CODE_LITERAL));
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.get("/api/identification")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .header("x-authenticate-key","uuid")
//                                .param("identificationCode","12345")
//                )
//                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
//                .andExpect(result -> {
//                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);
//                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
//                });
//    }
//
//
//
//
//
//}
