package com.ssg.usms.business.accident.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.dto.AccidentDto;
import com.ssg.usms.business.accident.service.AccidentService;
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
import java.util.ArrayList;
import java.util.List;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest(classes = EmbeddedRedis.class)
public class AccidentControllerRetrievingTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private StoreService storeService;
    @MockBean
    private AccidentService accidentService;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findAllByStoreId] : 점주 유저가 정상적인 파라미터로 요청시 이상 행동들이 조회된다.")
    @Test
    public void testFindAllByStoreIdWithValidParam() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int offset = 0;
        int size = 10;
        String startDate = "2024-01-14";
        String endDate = "2024-01-24";

        AccidentDto accident1 = new AccidentDto();
        accident1.setId(1L);
        accident1.setCctvId(1L);
        accident1.setBehavior(AccidentBehavior.COME_IN);
        accident1.setStartTimestamp(System.currentTimeMillis()-10000L);

        AccidentDto accident2 = new AccidentDto();
        accident2.setId(2L);
        accident2.setCctvId(1L);
        accident2.setBehavior(AccidentBehavior.COME_OUT);
        accident2.setStartTimestamp(System.currentTimeMillis());

        List<AccidentDto> accidentDtoList = new ArrayList<>();
        accidentDtoList.add(accident1);
        accidentDtoList.add(accident2);

        given(accidentService.findByStoreId(any(), any())).willReturn(accidentDtoList);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                                .param("behavior", "2", "3", "4")

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));

        verify(storeService, times(1)).validateOwnedStore(any(), any());
        verify(accidentService, times(1)).findByStoreId(any(), any());
    }


    @WithUserDetails("admin")
    @DisplayName("[findAllByStoreId] : 접근 권한 없는 유저가 접근시 예외 발생.")
    @Test
    public void testFindAllByStoreIdWithPermissionDeniedUser() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int offset = 0;
        int size = 10;
        String startDate = "2024-01-14";
        String endDate = "2024-01-24";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));

        verify(storeService, times(0)).validateOwnedStore(any(), any());
        verify(accidentService, times(0)).findByStoreId(any(), any());
    }

    @WithAnonymousUser
    @DisplayName("[findAllByStoreId] : 접근 권한 없는 유저가 접근시 예외 발생(2)")
    @Test
    public void testFindAllByStoreIdWithPermissionDeniedUser2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int offset = 0;
        int size = 10;
        String startDate = "2024-01-14";
        String endDate = "2024-01-24";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));

        verify(storeService, times(0)).validateOwnedStore(any(), any());
        verify(accidentService, times(0)).findByStoreId(any(), any());
    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findAllByStoreId] : 잘못된 offset 값으로 요청할 경우 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithInvalidOffset() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int offset = -5;
        int size = 10;
        String startDate = "2024-01-14";
        String endDate = "2024-01-24";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_OFFSET_FORMAT_CODE);
                })
        ;

        verify(storeService, times(0)).validateOwnedStore(any(), any());
        verify(accidentService, times(0)).findByStoreId(any(), any());
    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findAllByStoreId] : 잘못된 offset 값으로 요청할 경우 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithInvalidOffset2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int size = 10;
        String startDate = "2024-01-14";
        String endDate = "2024-01-24";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_OFFSET_FORMAT_CODE);
                })
        ;

        verify(storeService, times(0)).validateOwnedStore(any(), any());
        verify(accidentService, times(0)).findByStoreId(any(), any());
    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findAllByStoreId] : 잘못된 size 값으로 요청할 경우 예외가 발생한다.")
    @ValueSource(ints = {0, -1, -10})
    @ParameterizedTest
    public void testFindAllByStoreIdWithInvalidSize(int size) throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int offset = 0;

        String startDate = "2024-01-14";
        String endDate = "2024-01-24";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_SIZE_FORMAT_CODE);
                })
        ;

        verify(storeService, times(0)).validateOwnedStore(any(), any());
        verify(accidentService, times(0)).findByStoreId(any(), any());
    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findAllByStoreId] : 잘못된 size 값으로 요청할 경우 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithInvalidSize2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int offset = 10;
        String startDate = "2024-01-14";
        String endDate = "2024-01-24";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("startDate", startDate)
                                .param("endDate", endDate)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_SIZE_FORMAT_CODE);
                })
        ;

        verify(storeService, times(0)).validateOwnedStore(any(), any());
        verify(accidentService, times(0)).findByStoreId(any(), any());
    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findAllByStoreId] : 잘못된 date 값으로 요청할 경우 예외가 발생한다.")
    @ValueSource(strings = {"2024/01/24", "2024 01 24", "2024-13-24", "2024-12-32"})
    @ParameterizedTest
    public void testFindAllByStoreIdWithInvalidDateFormat(String dateFormat) throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int offset = 0;
        int size = 10;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", dateFormat)
                                .param("endDate", dateFormat)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(INVALID_DATE_FORMAT_CODE);
                })
        ;

        verify(storeService, times(0)).validateOwnedStore(any(), any());
        verify(accidentService, times(0)).findByStoreId(any(), any());
    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findAllByStoreId] : 잘못된 date 값으로 요청할 경우 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithInvalidDateFormat2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int offset = 10;
        int size = 10;
        String endDate = "2024-01-24";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("endDate", endDate)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(INVALID_DATE_FORMAT_CODE);
                })
        ;

        verify(storeService, times(0)).validateOwnedStore(any(), any());
        verify(accidentService, times(0)).findByStoreId(any(), any());
    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findAllByStoreId] : 잘못된 date 값으로 요청할 경우 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithInvalidDateFormat3() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int offset = 0;
        int size = 10;

        String startDate = "2024-01-24";
        String endDate = "2024-01-14";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(INVALID_DATE_FORMAT_CODE);
                })
        ;

        verify(storeService, times(0)).validateOwnedStore(any(), any());
        verify(accidentService, times(0)).findByStoreId(any(), any());
    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findAllByStoreId] : 존재하지 않는 매장일 경우 예외 발생")
    @Test
    public void testFindAllByStoreIdWithNotExistingStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int offset = 0;
        int size = 10;
        String startDate = "2024-01-14";
        String endDate = "2024-01-24";

        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_STORE_CODE);
                })
        ;

        verify(storeService, times(1)).validateOwnedStore(any(), any());
        verify(accidentService, times(0)).findByStoreId(any(), any());
    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findAllByStoreId] : 본인 소유가 아닌 매장 접근 시 예외 발생")
    @Test
    public void testFindAllByStoreIdWithNotOwnedStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        int offset = 0;
        int size = 10;
        String startDate = "2024-01-14";
        String endDate = "2024-01-24";

        willThrow(new NotOwnedStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/accidents", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_STORE_CODE);
                })
        ;

        verify(storeService, times(1)).validateOwnedStore(any(), any());
        verify(accidentService, times(0)).findByStoreId(any(), any());
    }
}