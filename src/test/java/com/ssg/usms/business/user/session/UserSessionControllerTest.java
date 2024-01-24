package com.ssg.usms.business.user.session;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.user.dto.HttpRequestModifyUserDto;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import com.ssg.usms.business.user.dto.SecurityState;
import com.ssg.usms.business.user.exception.AlreadyExistEmailException;
import com.ssg.usms.business.user.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.user.repository.UsmsUser;
import com.ssg.usms.business.user.service.UserService;
import com.ssg.usms.business.user.service.UserSessionService;
import com.ssg.usms.business.user.util.JwtUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.util.HashMap;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_NICKNAME_FORM;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest
public class UserSessionControllerTest {

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    private String token;

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @MockBean
    private UserSessionService userSessionService;

    @BeforeEach
    public void setup(){

        HashMap<String , String> hashMap = new HashMap<>();
        hashMap.put("code","0");
        hashMap.put("value","tkfka123@gmail.com");
        Long expiredMs = 3600000L; // 1 hour
        token = jwtUtil.createJwt(hashMap, expiredMs,"Identification");

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
    @DisplayName("세션 조회: 현재 세션과 일치하는 유저")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindUserSessionFromCurrentUserWithValidParam() throws Exception{

        given(userSessionService.findUserBySession()).willReturn(new HttpResponseUserDto());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/session")
                                .content("")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    result.getResponse().getContentAsString();
                });


    }

    @DisplayName("세션이 존재하지 않은 채 요청 시 403에러")
    @Test
    public void testFindUserSessionFromCurrentUserwWithNoSession() throws Exception {

        given(userSessionService.findUserBySession()).willThrow(new IllegalAccessException("허용되지 않은 세션"));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/session")
                                .content("")
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(result -> {
                    result.getResponse().getContentAsString();
                });
    }

    @DisplayName("유저 삭제 요청시 현재 세션과 일치하는 유저가 있고 성공적으로 삭제했을때")
    @WithUserDetails("storeOwner")
    @Test
    public void testDeleteUserSessionFromCurrentUserWithValidParam() throws Exception{

        doNothing().when(userSessionService).deleteUser(anyLong());
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/users/1")
                                .content("")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    result.getResponse().getContentAsString();
                });


    }
    @DisplayName("유저 삭제 요청시 현재 세션과 일치하는 유저가 없거나 권한이 없을때 401 리턴")
    @WithUserDetails("storeOwner")
    @Test
    public void testDeleteUserSessionFromCurrentUserWithNotAllowedSession() throws Exception{

        doThrow(new IllegalAccessException("error")).when(userSessionService).deleteUser(anyLong());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/users/1")
                                .content("")
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(result -> {
                    result.getResponse().getContentAsString();
                });

    }

    @DisplayName("세션없이 삭제요청을 했을때 403 리턴")
    @Test
    public void testDeleteUserSessionFromCurrentUserWithNoSession() throws Exception{

        doThrow(new IllegalAccessException("error")).when(userSessionService).deleteUser(anyLong());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/users/1")
                                .content("")
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(result -> {
                    result.getResponse().getContentAsString();
                });
    }

    @DisplayName("서버문제나 다른 문제로 삭제가 되지않았을때 500 리턴")
    @WithUserDetails("storeOwner")
    @Test
    public void testDeleteUserSessionFromCurrentUserSomeProblem() throws Exception{

        doThrow(new RuntimeException()).when(userSessionService).deleteUser(anyLong());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/users/1")
                                .content("")
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                });
    }



//    /////////////////////// 수정 기능


    @DisplayName("본인인증된 jwt로 회원 수정시 해당 유저가 존재할 경우 200리턴")
    @WithUserDetails("storeOwner")
    @Test
    public void TestSuccessModifyUserByJwt() throws Exception {

        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("nick123")
                .email("hello123@asdf.com")
                .phoneNumber("010-1234-4242")
                .password("tklfasdf123*")
                .securityState(SecurityState.BASIC)
                .build();

        given(userService.ModifyUser(anyLong(),any())).willReturn(new UsmsUser());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                                .header(HttpHeaders.AUTHORIZATION,token))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NO_CONTENT.value()));

    }

    @DisplayName("본인인증된 jwt로 회원 수정시 해당 유저가 존재할 경우 200리턴")
    @Test
    public void TestFailedModifyUserNoSession() throws Exception {

        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("nick123")
                .email("hello123@asdf.com")
                .phoneNumber("010-1234-4242")
                .password("tklfasdf123*")
                .securityState(SecurityState.BASIC)
                .build();

        given(userService.ModifyUser(anyLong(),any())).willReturn(new UsmsUser());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                                .header(HttpHeaders.AUTHORIZATION,token))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()));

    }


    @DisplayName("수정시 요청된 패스워드가 조건에 불충족할 경우 예외를 발생시킨다.")
    @WithUserDetails("storeOwner")
    @ParameterizedTest
    @ValueSource(strings={"as","asdf2123123","asdasdf@@@@@","sadfqeisdfasdfmaaaaaaaaaaaaaaaaaaaaaaaaaa2@aaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
    public void testPatchModifyWithNotAllowedPwd(String password) throws Exception {

        //given
        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("nick123")
                .email("hello123@asdf.com")
                .phoneNumber("010-1234-4242")
                .password(password)
                .securityState(SecurityState.BASIC)
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PASSWORD_FORM);
                });
    }


    @DisplayName("수정시 요청된 전화번호가 이미 존재하는 경우 예외를 발생시킨다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testPatchModifyAlreadyExistPhoneNumber() throws Exception {

        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("nick123")
                .email("hello123@asdf.com")
                .phoneNumber("010-1234-4242")
                .password("asdfsadf123*")
                .securityState(SecurityState.BASIC)
                .build();

        willThrow(new AlreadyExistPhoneNumException("이미 존재하는 전화번호 입니다.")).given(userService).ModifyUser(anyLong(),any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(409))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(ALREADY_EXIST_PHONE_NUM);
                });
    }

    @DisplayName("수정시 요청된 메일이 이미 존재하는 경우 예외를 발생시킨다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testPatchModifyAlreadyExistEmail() throws Exception {

        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("nick123")
                .email("hello123@asdf.com")
                .phoneNumber("010-1234-4242")
                .password("asdfsadf123*")
                .securityState(SecurityState.BASIC)
                .build();

        willThrow(new AlreadyExistEmailException("이미 존재하는 메일 입니다.")).given(userService).ModifyUser(anyLong(),any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(409))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
                });
    }

    @DisplayName("수정시 접근권한이 없는 경우 401리턴")
    @WithUserDetails("storeOwner")
    @Test
    public void testPatchModifyUnAuthrization() throws Exception {

        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("nick123")
                .email("hello123@asdf.com")
                .phoneNumber("010-1234-4242")
                .password("asdfsadf123*")
                .securityState(SecurityState.BASIC)
                .build();


        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token+"1234")
                )
                .andExpect(MockMvcResultMatchers.status().is(401))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_KEY);
                });
    }



    @DisplayName("수정시 요청된 이메일 양식이 잘못된 경우 예외를 발생시킨다.")
    @WithUserDetails("storeOwner")
    @ParameterizedTest
    @ValueSource(strings = {"invalid.email@com", "another@invalid@domain.com", "missingdomain@.com", "@invalidprefix.com", "missingusername@."})
    public void testPatchModifyWithNotAllowedEmail(String email) throws Exception {

        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("nick123")
                .email(email)
                .phoneNumber("010-1234-4242")
                .password("asdfsadf123*")
                .securityState(SecurityState.BASIC)
                .build();


        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_EMAIL_FORM);
                });

    }

    @DisplayName("수정시 요청된 전화번호 양식이 잘못 된 경우에 400에러 리턴.")
    @WithUserDetails("storeOwner")
    @ParameterizedTest
    @ValueSource(strings = {"12321", "101-1234-4845", "010-123-1223", "010-1323-123", "010*1212*1213"})
    public void testPatchModifyWithNotAllowedPhoneNumber(String phoneNumber) throws Exception {

        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("nick123")
                .email("email123@asdf.com")
                .phoneNumber(phoneNumber)
                .password("asdfsadf123*")
                .securityState(SecurityState.BASIC)
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PHONENUMBER_FORM);
                });

    }

    @DisplayName("수정시 요청된 닉네임 양식이 잘못된 경우 400 에러 리턴")
    @WithUserDetails("storeOwner")
    @ParameterizedTest
    @ValueSource(strings = {"12321ASDFASDF", "ASD@!@#0" })
    public void testPatchSignUpWithNotAllowedNickName(String nickname) throws Exception {

        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName(nickname)
                .email("eamas123@asdfa.com")
                .phoneNumber("010-1234-4242")
                .password("asdfsadf123*")
                .securityState(SecurityState.BASIC)
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_NICKNAME_FORM);
                });

    }


    @DisplayName("수정시 요청된 2차 비밀번호가 잘못된 경우 400 에러 리턴")
    @WithUserDetails("storeOwner")
    @ParameterizedTest
    @ValueSource(strings = {"12321ASDFASDF", "AAS12" ,"123123123","123" })
    public void testPatchAllowedSecondPassword(String secondpassword) throws Exception {

        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("asasdf123")
                .email("eamas123@asdfa.com")
                .phoneNumber("010-1234-4242")
                .password("asdfsadf123*")
                .securityState(SecurityState.BASIC)
                .secondPassword(secondpassword)
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });

    }

    @DisplayName("수정시 요청된 2차 비밀번호가 잘못된 경우 400 에러 리턴")
    @WithUserDetails("storeOwner")
    @ParameterizedTest
    @ValueSource(strings = {"12321ASDFASDF", "AAS12" ,"123123123","111252" })
    public void testPatchWithNotAllowedSecondPasswordCorrectSecurity(String secondpassword) throws Exception {

        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("asasdf123")
                .email("eamas123@asdfa.com")
                .phoneNumber("010-1234-4242")
                .password("asdfsadf123*")
                .securityState(SecurityState.SECONDPASSWORD)
                .secondPassword(secondpassword)
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });

    }

}
