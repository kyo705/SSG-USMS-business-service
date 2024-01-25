package com.ssg.usms.business.cctv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.cctv.dto.HttpRequestCreatingCctvDto;
import com.ssg.usms.business.cctv.service.CctvService;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.exception.NotOwnedStoreException;
import com.ssg.usms.business.store.service.StoreService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest
public class CctvControllerCreatingTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private StoreService storeService;
    @MockBean
    private CctvService cctvService;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("[createCctv] : 정상적인 cctv 생성 요청시 201 상태코드를 리턴한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testCreateCctv() throws Exception {

        //given
        String cctvName = "cctv 명칭 1";
        HttpRequestCreatingCctvDto requestBody = new HttpRequestCreatingCctvDto();
        requestBody.setName(cctvName);

        given(storeService.isAvailable(any())).willReturn(true);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users/{userId}/stores/{storeId}/cctvs", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()));
    }

    @DisplayName("[createCctv] : 접근 권한이 없는 유저로 cctv 생성 요청시 403 상태코드를 리턴한다.")
    @WithUserDetails("admin")
    @Test
    public void testCreateCctvWithPermissionDeniedUser() throws Exception {

        //given
        String cctvName = "cctv 명칭 1";
        HttpRequestCreatingCctvDto requestBody = new HttpRequestCreatingCctvDto();
        requestBody.setName(cctvName);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users/{userId}/stores/{storeId}/cctvs", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));
    }

    @DisplayName("[createCctv] : 접근 권한이 없는 유저로 cctv 생성 요청시 403 상태코드를 리턴한다.(2)")
    @WithAnonymousUser
    @Test
    public void testCreateCctvWithPermissionDeniedUser2() throws Exception {

        //given
        String cctvName = "cctv 명칭 1";
        HttpRequestCreatingCctvDto requestBody = new HttpRequestCreatingCctvDto();
        requestBody.setName(cctvName);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users/{userId}/stores/{storeId}/cctvs", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));
    }

    @DisplayName("[createCctv] : 허용되지 않은 CCTV 명칭으로 생성 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @NullAndEmptySource
    @ValueSource(strings = {"# 잘못된 cctv 명칭 1 !", "  "})
    @ParameterizedTest
    public void testCreateCctvWithInvalidCctvName(String invalidCctvName) throws Exception {

        //given
        HttpRequestCreatingCctvDto requestBody = new HttpRequestCreatingCctvDto();
        requestBody.setName(invalidCctvName);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users/{userId}/stores/{storeId}/cctvs", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(INVALID_CCTV_NAME_FORMAT_CODE);
                })
        ;
    }

    @DisplayName("[createCctv] : 존재하지 않는 storeId로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testCreateCctvWithNotExistingStore() throws Exception {

        //given
        String cctvName = "cctv 명칭 1";
        HttpRequestCreatingCctvDto requestBody = new HttpRequestCreatingCctvDto();
        requestBody.setName(cctvName);

        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users/{userId}/stores/{storeId}/cctvs", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_STORE_CODE);
                })
        ;
    }

    @DisplayName("[createCctv] : 본인 소유가 아닌 storeId로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testCreateCctvWithNotOwnedStore() throws Exception {

        //given
        String cctvName = "cctv 명칭 1";
        HttpRequestCreatingCctvDto requestBody = new HttpRequestCreatingCctvDto();
        requestBody.setName(cctvName);

        willThrow(new NotOwnedStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users/{userId}/stores/{storeId}/cctvs", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_STORE_CODE);
                })
        ;
    }

    @DisplayName("[createCctv] : 이용 불가능한 storeId로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testCreateCctvWithUnavailableStore() throws Exception {

        //given
        String cctvName = "cctv 명칭 1";
        HttpRequestCreatingCctvDto requestBody = new HttpRequestCreatingCctvDto();
        requestBody.setName(cctvName);

        given(storeService.isAvailable(any())).willReturn(false);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/users/{userId}/stores/{storeId}/cctvs", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(UNAVAILABLE_STORE_CODE);
                })
        ;
    }
}
