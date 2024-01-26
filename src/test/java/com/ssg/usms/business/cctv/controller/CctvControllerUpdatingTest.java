package com.ssg.usms.business.cctv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.cctv.dto.HttpRequestUpdatingCctvDto;
import com.ssg.usms.business.cctv.exception.NotExistingCctvException;
import com.ssg.usms.business.cctv.exception.NotOwnedCctvException;
import com.ssg.usms.business.cctv.service.CctvService;
import com.ssg.usms.business.config.EmbeddedRedis;
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
@SpringBootTest(classes = EmbeddedRedis.class)
public class CctvControllerUpdatingTest {

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

    @DisplayName("[update] : 정상적인 cctv 수정 요청시 204 상태코드를 리턴한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testUpdate() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        String cctvName = "cctv 명칭 1";
        HttpRequestUpdatingCctvDto requestBody = new HttpRequestUpdatingCctvDto();
        requestBody.setName(cctvName);

        given(storeService.isAvailable(storeId)).willReturn(true);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NO_CONTENT.value()));
    }

    @DisplayName("[update] : 접근 권한이 없는 유저로 cctv 수정 요청시 403 상태코드를 리턴한다.")
    @WithUserDetails("admin")
    @Test
    public void testUpdateWithPermissionDeniedUser() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        String cctvName = "cctv 명칭 1";
        HttpRequestUpdatingCctvDto requestBody = new HttpRequestUpdatingCctvDto();
        requestBody.setName(cctvName);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));
    }

    @DisplayName("[update] : 접근 권한이 없는 유저로 cctv 수정 요청시 403 상태코드를 리턴한다.(2)")
    @WithAnonymousUser
    @Test
    public void testUpdateWithPermissionDeniedUser2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        String cctvName = "cctv 명칭 1";
        HttpRequestUpdatingCctvDto requestBody = new HttpRequestUpdatingCctvDto();
        requestBody.setName(cctvName);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));
    }

    @DisplayName("[update] : 허용되지 않은 CCTV 명칭으로 수정 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @NullAndEmptySource
    @ValueSource(strings = {"# 잘못된 cctv 명칭 1 !", "  "})
    @ParameterizedTest
    public void testUpdateWithInvalidCctvName(String invalidCctvName) throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        HttpRequestUpdatingCctvDto requestBody = new HttpRequestUpdatingCctvDto();
        requestBody.setName(invalidCctvName);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
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

    @DisplayName("[update] : 존재하지 않는 storeId로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testUpdateWithNotExistingStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        String cctvName = "cctv 명칭 1";
        HttpRequestUpdatingCctvDto requestBody = new HttpRequestUpdatingCctvDto();
        requestBody.setName(cctvName);

        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
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

    @DisplayName("[update] : 본인 소유가 아닌 storeId로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testUpdateWithNotOwnedStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        String cctvName = "cctv 명칭 1";
        HttpRequestUpdatingCctvDto requestBody = new HttpRequestUpdatingCctvDto();
        requestBody.setName(cctvName);

        willThrow(new NotOwnedStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
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

    @DisplayName("[update] : 이용 불가능한 storeId로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testUpdateWithUnavailableStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        String cctvName = "cctv 명칭 1";
        HttpRequestUpdatingCctvDto requestBody = new HttpRequestUpdatingCctvDto();
        requestBody.setName(cctvName);

        given(storeService.isAvailable(storeId)).willReturn(false);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
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

    @DisplayName("[update] : 존재하지 않는 CCTV일 경우 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testUpdateWithNotExistingCctv() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        String cctvName = "cctv 명칭 1";
        HttpRequestUpdatingCctvDto requestBody = new HttpRequestUpdatingCctvDto();
        requestBody.setName(cctvName);

        willThrow(new NotExistingCctvException()).given(cctvService).validateOwnedCctv(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_CCTV_CODE);
                })
        ;
    }

    @DisplayName("[update] : 본인 소유가 아닌 CCTV를 요청할 경우 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testUpdateWithNotOwnedCctv() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        String cctvName = "cctv 명칭 1";
        HttpRequestUpdatingCctvDto requestBody = new HttpRequestUpdatingCctvDto();
        requestBody.setName(cctvName);

        willThrow(new NotOwnedCctvException()).given(cctvService).validateOwnedCctv(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_CCTV_CODE);
                })
        ;
    }
}
