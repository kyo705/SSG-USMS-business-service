package com.ssg.usms.business.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.user.dto.*;
import com.ssg.usms.business.user.exception.AlreadyExistEmailException;
import com.ssg.usms.business.user.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.user.exception.AlreadyExistUsernameException;
import com.ssg.usms.business.user.repository.UsmsUser;
import com.ssg.usms.business.user.service.UserService;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ActiveProfiles("test")
@SpringBootTest(classes = EmbeddedRedis.class)
public class UserControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    private String token;


    @BeforeEach
    public void setup() {

        HashMap<String , String> hashMap = new HashMap<>();
        hashMap.put("code","0");
        hashMap.put("value","tkfka123@gmail.com");
        Long expiredMs = 3600000L; // 1 hour
        token = jwtUtil.createJwt(hashMap, expiredMs,"Identification");

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
        dto.setPhoneNumber("010-1234-2412");
        dto.setEmail("tkfka123@gmail.com");
        dto.setNickname("hihello");


        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(201));

    }

    @DisplayName("요청된 패스워드가 조건에 불충족할 경우 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings={"as","asdf2123123","asdasdf@@@@@","sadfqeisdfasdfmaaaaaaaaaaaaaaaaaaaaaaaaaa2@aaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
    public void testPostSignUpWithNotAllowedPwd(String password) throws Exception {

        //given
        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("aksenaksen");
        dto.setPassword(password);
        dto.setEmail("aksenaksen@123asdf.com");
        dto.setPhoneNumber("010-1513-5454");
        dto.setNickname("aksenaksen");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
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





    @DisplayName("요청된 아이디가 조건에 불충족할 경우 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"a","abc1234@","asdsssssssssssssssss12312ddddddddddddddddddd3"})
    public void testPostSignUpWithNotAllowedId(String id) throws Exception {
        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername(id);
        dto.setPassword("akaasdf123@");
        dto.setEmail("tkfka123@gmail.com");
        dto.setPhoneNumber("010-1513-5454");
        dto.setNickname("heeee");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_USERNAME_FORM);
                });
    }

    @DisplayName("요청된 아이디가 이미 존재하는 경우 예외를 발생시킨다.")
    @Test
    public void testPostSignUpAlreadyExistId() throws Exception {

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("tmasdfasdf");
        dto.setPassword("askaasddf123*");
        dto.setEmail("tkfka123@gmail.com");
        dto.setPhoneNumber("010-1513-5454");
        dto.setNickname("hellow");

        willThrow(new AlreadyExistUsernameException("이미 존재하는 전화번호 입니다.")).given(userService).signUp(any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(409))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(ALREADY_EXIST_USERNAME);
                });
    }



    @DisplayName("요청된 전화번호가 이미 존재하는 경우 예외를 발생시킨다.")
    @Test
    public void testPostSignUpAlreadyExistPhoneNumber() throws Exception {

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("tmasdfasdf");
        dto.setPassword("askaasddf123*");
        dto.setEmail("tkfka123@gmail.com");
        dto.setPhoneNumber("010-1513-5454");
        dto.setNickname("hellow");

        willThrow(new AlreadyExistPhoneNumException("이미 존재하는 전화번호 입니다.")).given(userService).signUp(any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
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
    @DisplayName("요청된 dto에 맞지않게 요청된 경우 예외 처리")
    @Test
    public void testInValidSignupwitNotMatchedDto() throws Exception {
        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();

        dto.setUsername("tmasdfasdf");
        dto.setPassword("askaasddf123*");
        dto.setEmail("aksenaksen@asdf.com");
        dto.setPhoneNumber("010-1513-5434");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(400));
    }



    @DisplayName("요청된 이메일 양식이 잘못된 경우 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"invalid.email@com", "another@invalid@domain.com", "missingdomain@.com", "@invalidprefix.com", "missingusername@."})
    public void testPostSignUpWithNotAllowedEmail(String email) throws Exception {

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("aksenakse1231");
        dto.setPassword("asdf112323$");
        dto.setEmail(email);
        dto.setPhoneNumber("010-1513-5454");
        dto.setNickname("hello");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
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

    @DisplayName("요청된 전화번호 양식이 잘못 된 경우에 400에러 리턴.")
    @ParameterizedTest
    @ValueSource(strings = {"12321", "101-1234-4845", "010-123-1223", "010-1323-123", "010*1212*1213"})
    public void testPostSignUpWithNotAllowedPhoneNumber(String phoneNumber) throws Exception {

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("aksenakse1231");
        dto.setPassword("asdf112323$");
        dto.setEmail("as123@asdf.com");
        dto.setPhoneNumber(phoneNumber);
        dto.setNickname("hello");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
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

    @DisplayName("요청된 닉네임 양식이 잘못된 경우 400 에러 리턴")
    @ParameterizedTest
    @ValueSource(strings = {"12321ASDFASDF", "ASD@!@#0" })
    public void testPostSignUpWithNotAllowedNickName(String nickname) throws Exception {

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("aksenakse1231");
        dto.setPassword("asdf112323$");
        dto.setEmail("as123@asdf.com");
        dto.setPhoneNumber("010-1234-5253");
        dto.setNickname(nickname);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
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



    @DisplayName("요청된 본인인증 키가 잘못된 경우 예외를 발생시킨다.")
    @Test
    public void testPostSignUpWithNotAllowedKey() throws Exception {

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("aksenaksen3");
        dto.setPassword("hashedpasord123@");
        dto.setPhoneNumber("010-1234-2414");
        dto.setEmail("asdf123@naer.com");
        dto.setNickname("hihello");


        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token+"asdf")
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
        dto.setPhoneNumber("010-1234-2414");
        dto.setEmail("tkfka123@gmail.com");
        dto.setNickname("hihello");

        willThrow((new RuntimeException())).given(userService).signUp(any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType("application/json; charset=utf-8")
                                .header("Authorization",token)
                )
                .andExpect(MockMvcResultMatchers.status().is(500));
    }




    @DisplayName("본인인증된 jwt로 회원 조회시 해당 유저가 존재할 경우 200과 body로 dto를 리턴")
    @Test
    public void TestSuccessFindUserByJwt() throws Exception {


        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .username("hello")
                .email("tkfka123@gmail.com")
                .id(1L)
                .nickname("asdf")
                .securityState(0)
                .build();

        given(userService.findUserByValue(any())).willReturn(dto);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/user")
                                .header(HttpHeaders.AUTHORIZATION,token))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(dto)))
                .andExpect(result -> {
                    String actualToken = result.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
                    assertEquals(token,actualToken);
                });

    }






    @DisplayName("본인인증된 jwt로 회원 조회시 검증되지 않은 유저일때 401리턴")
    @Test
    public void TestFailFindUserByJwt() throws Exception {


        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .username("hello")
                .email("tkfka123@gmail.com")
                .id(1L)
                .nickname("asdf")
                .securityState(0)
                .build();

        given(userService.findUserByValue(any())).willReturn(dto);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/user")
                                .header(HttpHeaders.AUTHORIZATION,token+"asdf"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()));
    }


    @DisplayName("세션 조회: 현재 세션과 일치하는 유저")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindUserSessionFromCurrentUserWithValidParam() throws Exception{

        given(userService.findUserBySession()).willReturn(new HttpResponseUserDto());

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

        given(userService.findUserBySession()).willThrow(new IllegalAccessException("허용되지 않은 세션"));

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

        doNothing().when(userService).deleteUser(anyLong());
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

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/users/2")
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

        doThrow(new RuntimeException()).when(userService).deleteUser(anyLong());

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
                .nickname("nick123")
                .email("hello123@asdf.com")
                .phoneNumber("010-1234-4242")
                .password("tklfasdf123*")
                .securityState(SecurityState.BASIC)
                .build();

        given(userService.modifyUser(anyLong(),any())).willReturn(new UsmsUser());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                                .header(HttpHeaders.AUTHORIZATION,token))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NO_CONTENT.value()));

    }

    @DisplayName("본인인증된 jwt로 회원 수정시 해당 유저가 존재하지 않는 경우 경우 401리턴")
    @Test
    public void TestFailedModifyUserNoSession() throws Exception {

        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .nickname("nick123")
                .email("hello123@asdf.com")
                .phoneNumber("010-1234-4242")
                .password("tklfasdf123*")
                .securityState(SecurityState.BASIC)
                .build();

        given(userService.modifyUser(anyLong(),any())).willReturn(new UsmsUser());

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
                .nickname("nick123")
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
                .nickname("nick123")
                .email("hello123@asdf.com")
                .phoneNumber("010-1234-4242")
                .password("asdfsadf123*")
                .securityState(SecurityState.BASIC)
                .build();

        willThrow(new AlreadyExistPhoneNumException("이미 존재하는 전화번호 입니다.")).given(userService).modifyUser(anyLong(),any());

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
                .nickname("nick123")
                .email("hello123@asdf.com")
                .phoneNumber("010-1234-4242")
                .password("asdfsadf123*")
                .securityState(SecurityState.BASIC)
                .build();

        willThrow(new AlreadyExistEmailException("이미 존재하는 메일 입니다.")).given(userService).modifyUser(anyLong(),any());

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
                .nickname("nick123")
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
                .nickname("nick123")
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
                .nickname("nick123")
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
                .nickname(nickname)
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
                .nickname("asasdf123")
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
                .nickname("asasdf123")
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

    @DisplayName("회원 중복 확인시에 제대로된 양식으로 요청한 경우")
    @Test
    public void testCheckUserSuccessed() throws Exception{

        String email = "ver123@nasdf.com";
        String username = "tmasdfasdf";
        String phoneNumber = "010-1532-5434";

        doNothing().when(userService).checkExistUser(any(),any(),any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/check/users")
                                .param("username",username)
                                .param("email",email)
                                .param("phoneNumber",phoneNumber)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NO_CONTENT.value()));
    }

    @DisplayName("회원 중복 확인시에 제대로된 양식으로 요청한 경우")
    @Test
    public void testCheckUserSuccessedwithNoParam() throws Exception{

        String email = "ver123@nasdf.com";
        String username = "tmasdfasdf";
        String phoneNumber = "010-15132-5434";

        doNothing().when(userService).checkExistUser(any(),any(),any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/check/users")
                                .param("username",username)
                                .param("email",email)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NO_CONTENT.value()));
    }

    @DisplayName("회원 중복 확인시에 제대로된 양식으로 요청하지 않은 경우")
    @Test
    public void testCheckUserFailed() throws Exception{
        String email = "ver123@nasdf.com";

        String username = "tmasdfasdf";
        String password ="askaasddf123*";
        String phoneNumber = "010-15132-5434";

        doNothing().when(userService).checkExistUser(any(),any(),any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/check/users")
                                .param("username",username)
                                .param("email",email)
                                .param("phoneNumber",phoneNumber)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @DisplayName("회원 중복 확인시에 제대로된 양식으로 요청하지 않은 경우 400리턴")
    @Test
    public void testCheckUserFailedEmail() throws Exception{


        String username = "tmasdfasdf";
        String password ="askaasddf123*";
        String email = "ver.com";
        String phoneNumber = "010-1513-5434";

        doNothing().when(userService).checkExistUser(any(),any(),any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/check/users")
                                .param("username",username)
                                .param("email",email)
                                .param("phoneNumber",phoneNumber)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));
    }


    @DisplayName("회원 중복 확인시에 회원이 존재하는 경우 409 리턴")

    @Test
    public void testCheckUserFailedEmailDuplicateUser() throws Exception{

        String username = "tmasdfasdf";
        String password ="askaasddf123*";
        String email = "aksenaksen@asdf.com";
        String phoneNumber = "010-1513-5434";

        doThrow(new AlreadyExistUsernameException("아이디 중복")).when(userService).checkExistUser(any(),any(),any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/check/users")
                                .param("username",username)
                                .param("email",email)
                                .param("phoneNumber",phoneNumber)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CONFLICT.value()));
    }

    @DisplayName("회원 중복 확인시에 회원이 존재하는 경우 409 리턴")
    @Test
    public void testCheckUserFailedEmailDuplicateUserEmail() throws Exception{

        String username = "tmasdfasdf";
        String password ="askaasddf123*";
        String email = "aksenaksen@asdf.com";
        String phoneNumber = "010-1513-5434";


        doThrow(new AlreadyExistEmailException("이메일 중복")).when(userService).checkExistUser(any(),any(),any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/check/users")
                                .param("username",username)
                                .param("email",email)
                                .param("phoneNumber",phoneNumber)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CONFLICT.value()));
    }

    @DisplayName("회원 중복 확인시에 회원이 존재하는 경우 409 리턴")
    @Test
    public void testCheckUserFailedEmailDuplicatePhone() throws Exception{

        String username = "tmasdfasdf";
        String password ="askaasddf123*";
        String email = "aksenaksen@asdf.com";
        String phoneNumber = "010-1513-5434";



        doThrow(new AlreadyExistPhoneNumException("전화번호 중복")).when(userService).checkExistUser(any(),any(),any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/check/users")
                                .param("username",username)
                                .param("email",email)
                                .param("phoneNumber",phoneNumber)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CONFLICT.value()));
    }

    @DisplayName("회원 중복 확인시에 파라미터가 들어오지 않은 경우")
    @Test
    public void testCheckUserFailedEmailDuplicateNoParam() throws Exception{

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/check/users")

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));
    }





}
