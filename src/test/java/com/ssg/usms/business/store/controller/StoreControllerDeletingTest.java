package com.ssg.usms.business.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.exception.NotOwnedStoreException;
import com.ssg.usms.business.store.service.StoreService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_EXISTING_STORE_CODE;
import static com.ssg.usms.business.constant.CustomStatusCode.NOT_OWNED_STORE_CODE;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EmbeddedRedis.class)
public class StoreControllerDeletingTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private StoreService storeService;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @WithUserDetails("storeOwner")
    @DisplayName("[deleteStore] : 정상적인 파라미터로 요청시 204 상태코드를 리턴한다.")
    @Test
    public void testDeletingWithValidParam() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(204));

    }

    @WithUserDetails("admin")
    @DisplayName("[deleteStore] : 접근 권한 없는 유저로 요청시 예외가 발생한다.")
    @Test
    public void testDeletingWithPermissionDeniedUser() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(403));

    }

    @WithAnonymousUser
    @DisplayName("[deleteStore] : 접근 권한 없는 유저로 요청시 예외가 발생한다. (2)")
    @Test
    public void testDeletingWithPermissionDeniedUser2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(403));

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[deleteStore] : 존재하지 않은 매장 id로 요청시 예외가 발생한다.")
    @Test
    public void testDeletingWithNotExistingStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(storeId, userId);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_STORE_CODE);
                })
        ;

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[deleteStore] : 본인 소유의 매장이 아닌 경우 예외가 발생한다.")
    @Test
    public void testDeletingWithNotOwnedStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        willThrow(new NotOwnedStoreException()).given(storeService).validateOwnedStore(storeId, userId);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_STORE_CODE);
                })
        ;
    }
}
