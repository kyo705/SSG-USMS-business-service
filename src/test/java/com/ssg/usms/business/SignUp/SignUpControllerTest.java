package com.ssg.usms.business.SignUp;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.User.exception.*;
import com.ssg.usms.business.User.persistence.HttpRequestSignUpDto;
import com.ssg.usms.business.User.service.SignUpService;
import com.ssg.usms.business.User.util.JwtUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SignUpControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private SignUpService signUpService;
    @MockBean
    private JwtUtil jwtUtil;


    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("성공한경우")
    @Test
    public void testPostSignUp() throws Exception {
        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("aksenaksen3");
        dto.setPassword("hashedpasord123@");
        dto.setPhoneNum("010-1234-2412");
        dto.setEmail("asdf123@naer.com");
        dto.setNickname("hihello");

        given(jwtUtil.VerifyToken(any())).willReturn(true);
        given(signUpService.SignUp(any()))
                .willReturn(true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                )
                .andExpect(MockMvcResultMatchers.status().is(201));

    }

    @DisplayName("요청된 패스워드가 조건에 불충족할 경우 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings={"as","asdf2123123","asdasdf@@@@@","sadfqeisdfasdfmaaaaaaaaaaaaaaaaaaaaaaaaaa2@aaa"})
    public void testPostSignUpWithNotAllowedPwd(String password) throws Exception {

        //given
        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("aksenaksen");
        dto.setPassword(password);
        dto.setEmail("aksenaksen@123asdf.com");
        dto.setPhoneNum("101-1513-5454");
        dto.setNickname("aksenaksen");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PASSWORD_FORM);
                });
    }





    @DisplayName("요청된 아이디가 조건에 불충족할 경우 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"a","abc1234@","asdsssssssssssssssss12312ddddddddddddddddddd3"})
    public void testPostSignUpWithNotAllowedId(String id) throws Exception {
        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername(id);
        dto.setPassword("akaasdf123@");
        dto.setEmail("aksenaksen@asdf.com");
        dto.setPhoneNum("101-1513-5454");
        dto.setNickname("heeee");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_ID_FORM);
                });
    }

    @DisplayName("요청된 아이디가 이미 존재하는 경우 예외를 발생시킨다.")
    @Test
    public void testPostSignUpAlreadyExistId() throws Exception {

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("tmasdfasdf");
        dto.setPassword("askaasddf123*");
        dto.setEmail("aksenaksen@asdf.com");
        dto.setPhoneNum("101-1513-5454");
        dto.setNickname("hellow");

        given(signUpService.SignUp(any())).willThrow(new AlreadyExistIdException("이미 존재하는 아이디 입니다."));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                )
                .andExpect(MockMvcResultMatchers.status().is(409))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(ALREADY_EXIST_ID);
                });
    }



    @DisplayName("요청된 전화번호가 이미 존재하는 경우 예외를 발생시킨다.")
    @Test
    public void testPostSignUpAlreadyExistPhoneNumber() throws Exception {

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("tmasdfasdf");
        dto.setPassword("askaasddf123*");
        dto.setEmail("aksenaksen@asdf.com");
        dto.setPhoneNum("101-1513-5454");
        dto.setNickname("hellow");

        given(signUpService.SignUp(any())).willThrow(new AlreadyExistPhoneNumException("이미 존재하는 전화번호 입니다."));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                )
                .andExpect(MockMvcResultMatchers.status().is(409))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(ALREADY_EXIST_PHONE_NUM);
                });
    }



    @DisplayName("요청된 이메일 양식이 잘못된 경우 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"invalid.email@com", "another@invalid@domain.com", "missingdomain@.com", "@invalidprefix.com", "missingusername@."})
    public void testPostSignUpWithNotAllowedEmail(String email) throws Exception {

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("aksenakse1231");
        dto.setPassword("asdf112323$");
        dto.setEmail(email);
        dto.setPhoneNum("101-1513-5454");
        dto.setNickname("hello");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_EMAIL_FORM);
                });

    }


    @DisplayName("요청된 본인인증 키가 잘못된 경우 예외를 발생시킨다.")
    @Test
    public void testPostSignUpWithNotAllowedKey() throws Exception {

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("aksenaksen3");
        dto.setPassword("hashedpasord123@");
        dto.setPhoneNum("010-1234-2414");
        dto.setEmail("asdf123@naer.com");
        dto.setNickname("hihello");

        given(jwtUtil.VerifyToken(any())).willThrow(new NotAllowedKeyExcetpion("잘못된 본인인증 키 입니다."));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                )
                .andExpect(MockMvcResultMatchers.status().is(401))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_KEY);
                });

    }
    @DisplayName("나머지 모든 에러가 있을때")
    @Test
    public void testPostSignUpWithServerError() throws Exception {

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("aksenaksen3");
        dto.setPassword("hashedpasord123@");
        dto.setPhoneNum("010-1234-2414");
        dto.setEmail("asdf123@naer.com");
        dto.setNickname("hihello");

        given(signUpService.SignUp(any())).willThrow(new RuntimeException());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                )
                .andExpect(MockMvcResultMatchers.status().is(500));
    }

}

