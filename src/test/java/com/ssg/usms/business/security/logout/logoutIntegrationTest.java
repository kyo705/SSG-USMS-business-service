package com.ssg.usms.business.security.logout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.device.repository.SpringJpaDataDeviceRepository;
import com.ssg.usms.business.security.login.persistence.ResponseLogoutDto;
import com.ssg.usms.business.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


@Transactional
@ActiveProfiles("test")
@SpringBootTest(classes = EmbeddedRedis.class)
public class logoutIntegrationTest {

    @Autowired
    private BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Autowired
    private SpringJpaDataDeviceRepository jpaDataDeviceRepository;

    @Autowired
    private UserRepository repository;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }


    @WithUserDetails(value = "storeOwner",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("기존에 로그인된 세션이 있는 경우에 로그아웃을 시도할 경우 200 코드를 리턴한다.")
    @Test
    public void testLoginWithAuthorizedUserInfo() throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    ResponseLogoutDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseLogoutDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(jpaDataDeviceRepository.findAll().size()).isEqualTo(0);
                });
    }

    @DisplayName("세션이 없을때 로그아웃을 시도하는 경우 400을 리턴한다.")
    @Test
    public void testLoginWithInvalidPassword() throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("")
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ResponseLogoutDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseLogoutDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });

    }

}
