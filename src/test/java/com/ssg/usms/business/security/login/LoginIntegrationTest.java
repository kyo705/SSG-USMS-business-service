package com.ssg.usms.business.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.device.repository.DeviceRepository;
import com.ssg.usms.business.device.repository.SpringJpaDataDeviceRepository;
import com.ssg.usms.business.device.repository.UsmsDevice;
import com.ssg.usms.business.security.login.persistence.RequestLoginDto;
import com.ssg.usms.business.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;



@Slf4j
@Transactional
@ActiveProfiles("test")
@SpringBootTest(classes = EmbeddedRedis.class)
public class LoginIntegrationTest {

    @Autowired
    private UserRepository repository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private SpringJpaDataDeviceRepository jpaDataDeviceRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {



        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }


    @DisplayName("인증이 완료된 유저로 로그인 시도할 경우 200 상태코드를 리턴한다.")
    @Test
    public void testLoginWithAuthorizedUserInfo() throws Exception {

        jpaDataDeviceRepository.deleteByUserid(1L);
        RequestLoginDto requestBody = new RequestLoginDto();
        requestBody.setUsername("storeOwner");
        requestBody.setPassword("1234567890a*");
        requestBody.setToken("newtoken");
        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    Optional<UsmsDevice> userDevice = jpaDataDeviceRepository.findById(2L);
                    assertNotNull(userDevice);
                    assertEquals(requestBody.getToken(),userDevice.get().getToken());
                });

    }

    @DisplayName("인증이 완료된 유저로 로그인 시도할 경우 토큰값이 빠졌으면 400 상태코드를 리턴. AuthenticationServiceException을 던진다.")
    @Test
    public void testLoginWithAuthorizedUserInfoNotincludeToken() throws Exception {

        RequestLoginDto requestBody = new RequestLoginDto();
        requestBody.setUsername("storeOwner");
        requestBody.setPassword("1234567890a*");
//        requestBody.setToken("newtoken");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));

    }

    @DisplayName("존재하지 않는 이메일로 로그인 시도할 경우 400 상태코드를 리턴한다.")
    @Test
    public void testLoginWithNotExistingEmail() throws Exception {

        RequestLoginDto requestBody = new RequestLoginDto();
        requestBody.setUsername("storeOwner232");
        requestBody.setPassword("1234567890a*");
        requestBody.setToken("newtoken");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));

    }
    @DisplayName("비밀번호가 부정확한 요청으로 로그인 시도할 경우 400 상태코드를 리턴한다.")
    @Test
    public void testLoginWithInvalidPassword() throws Exception {

        RequestLoginDto requestBody = new RequestLoginDto();
        requestBody.setUsername("storeOwner");
        requestBody.setPassword("1234567890123213a*");
        requestBody.setToken("newtoken");
        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));

    }








}
