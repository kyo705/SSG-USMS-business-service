package com.ssg.usms.business.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.user.dto.HttpRequestModifyUserDto;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import com.ssg.usms.business.user.dto.SecurityState;
import com.ssg.usms.business.user.exception.AlreadyExistEmailException;
import com.ssg.usms.business.user.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.user.exception.NotAllowedSessionIdException;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;

@ActiveProfiles("test")

@SpringBootTest
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



    @DisplayName("본인인증된 jwt로 회원 조회시 해당 유저가 존재할 경우 200과 body로 dto를 리턴")
    @Test
    public void TestSuccessFindUserByJwt() throws Exception {


        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .username("hello")
                .email("tkfka123@gmail.com")
                .id(1L)
                .personName("asdf")
                .securityState(SecurityState.BASIC)
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
                .personName("asdf")
                .securityState(SecurityState.BASIC)
                .build();

        given(userService.findUserByValue(any())).willReturn(dto);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/user")
                                .header(HttpHeaders.AUTHORIZATION,token+"asdf"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()));
    }







}
