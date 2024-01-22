package com.ssg.usms.business.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
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
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

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

    @DisplayName("[deleteStore] : 존재하지 않은 매장 id로 요청시 예외가 발생한다.")
    @Test
    public void testDeletingWithNotExistingStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        willThrow(new NotExistingStoreException()).given(storeService).delete(storeId, userId);

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

    @DisplayName("[deleteStore] : 본인 소유의 매장이 아닌 경우 예외가 발생한다.")
    @Test
    public void testDeletingWithNotOwnedStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        willThrow(new NotOwnedStoreException()).given(storeService).delete(storeId, userId);

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
