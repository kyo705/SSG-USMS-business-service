package com.ssg.usms.business.warning.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.exception.NotOwnedStoreException;
import com.ssg.usms.business.store.service.StoreService;
import com.ssg.usms.business.warning.RegionWarningTestSetup;
import com.ssg.usms.business.warning.dto.RegionWarningDto;
import com.ssg.usms.business.warning.service.RegionWarningService;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest()
public class RegionWarningControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;
    @MockBean
    private RegionWarningService mockRegionWarningService;
    @MockBean
    private StoreService mockStoreService;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @WithUserDetails("storeOwner")
    @DisplayName("정상적인 파라미터로 요청시 지역 경고 알림 정보들이 조회된다.")
    @Test
    public void testFindAllByStoreIdWithValidParam() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 20;

        List<RegionWarningDto> list = RegionWarningTestSetup.getRegionWarning("서울특별시 강남구").stream().map(RegionWarningDto::new).collect(Collectors.toList());

        given(mockStoreService.isAvailable(storeId)).willReturn(true);
        given(mockRegionWarningService.findByRegion(storeId, null, null, offset, size)).willReturn(list);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                )
                .andExpect(MockMvcResultMatchers.status().is(200))
        ;
    }

    @WithUserDetails("storeOwner")
    @DisplayName("정상적인 파라미터로 요청시 지역 경고 알림 정보들이 조회된다.")
    @Test
    public void testFindAllByStoreIdWithValidParam2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 20;
        String startDate = "2024-01-26";

        List<RegionWarningDto> list = RegionWarningTestSetup.getRegionWarning("서울특별시 강남구").stream().map(RegionWarningDto::new).collect(Collectors.toList());

        given(mockStoreService.isAvailable(storeId)).willReturn(true);
        given(mockRegionWarningService.findByRegion(storeId, startDate, null, offset, size)).willReturn(list);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(200))
        ;
    }

    @WithUserDetails("storeOwner")
    @DisplayName("정상적인 파라미터로 요청시 지역 경고 알림 정보들이 조회된다.")
    @Test
    public void testFindAllByStoreIdWithValidParam3() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 20;
        String endDate = "2024-01-26";

        List<RegionWarningDto> list = RegionWarningTestSetup.getRegionWarning("서울특별시 강남구").stream().map(RegionWarningDto::new).collect(Collectors.toList());

        given(mockStoreService.isAvailable(storeId)).willReturn(true);
        given(mockRegionWarningService.findByRegion(storeId, null, endDate, offset, size)).willReturn(list);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(200))
        ;
    }

    @WithUserDetails("storeOwner")
    @DisplayName("정상적인 파라미터로 요청시 지역 경고 알림 정보들이 조회된다.")
    @Test
    public void testFindAllByStoreIdWithValidParam4() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 20;
        String startDate = "2024-01-22";
        String endDate = "2024-01-26";

        List<RegionWarningDto> list = RegionWarningTestSetup.getRegionWarning("서울특별시 강남구").stream().map(RegionWarningDto::new).collect(Collectors.toList());

        given(mockStoreService.isAvailable(storeId)).willReturn(true);
        given(mockRegionWarningService.findByRegion(storeId, startDate, endDate, offset, size)).willReturn(list);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(200))
        ;
    }

    @WithUserDetails("admin")
    @DisplayName("접근 권한 없는 유저로 접근한 경우 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithPermissionDeniedUser() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 20;
        String startDate = "2024-01-22";
        String endDate = "2024-01-26";
        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(403))
        ;
    }

    @WithAnonymousUser
    @DisplayName("접근 권한 없는 유저로 접근한 경우 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithPermissionDeniedUser2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 20;
        String startDate = "2024-01-22";
        String endDate = "2024-01-26";
        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(403))
        ;
    }

    @WithUserDetails("storeOwner")
    @DisplayName("잘못된 페이지 offset으로 요청시 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithInvalidOffset() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = -5;
        int size = 20;
        String startDate = "2024-01-22";
        String endDate = "2024-01-26";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_OFFSET_FORMAT_CODE);
                })
        ;
    }

    @WithUserDetails("storeOwner")
    @DisplayName("잘못된 페이지 offset으로 요청시 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithInvalidOffset2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int size = 20;
        String startDate = "2024-01-22";
        String endDate = "2024-01-26";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_OFFSET_FORMAT_CODE);
                })
        ;
    }

    @WithUserDetails("storeOwner")
    @DisplayName("잘못된 페이지 사이즈로 요청시 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithInvalidSize() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 0;
        String startDate = "2024-01-22";
        String endDate = "2024-01-26";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_SIZE_FORMAT_CODE);
                })
        ;
    }

    @WithUserDetails("storeOwner")
    @DisplayName("잘못된 페이지 사이즈로 요청시 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithInvalidSize2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        String startDate = "2024-01-22";
        String endDate = "2024-01-26";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_SIZE_FORMAT_CODE);
                })
        ;
    }

    @WithUserDetails("storeOwner")
    @DisplayName("잘못된 페이지 사이즈로 요청시 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithInvalidDateFlow() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 20;
        String startDate = "2024-01-30";
        String endDate = "2024-01-26";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(INVALID_DATE_FORMAT_CODE);
                })
        ;
    }

    @WithUserDetails("storeOwner")
    @DisplayName("존재하지 않은 매장 id로 요청시 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithNotExistingStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 20;
        String startDate = "2024-01-10";
        String endDate = "2024-01-26";

        willThrow(new NotExistingStoreException()).given(mockStoreService).validateOwnedStore(storeId, userId);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(NOT_EXISTING_STORE_CODE);
                })
        ;
    }

    @WithUserDetails("storeOwner")
    @DisplayName("본인 소유가 아닌 매장 id로 요청시 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithNotOwnedStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 20;
        String startDate = "2024-01-10";
        String endDate = "2024-01-26";

        willThrow(new NotOwnedStoreException()).given(mockStoreService).validateOwnedStore(storeId, userId);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(NOT_OWNED_STORE_CODE);
                })
        ;
    }

    @WithUserDetails("storeOwner")
    @DisplayName("정지된 매장의 id로 요청시 예외가 발생한다.")
    @Test
    public void testFindAllByStoreIdWithUnavailableStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 20;
        String startDate = "2024-01-10";
        String endDate = "2024-01-26";

        given(mockStoreService.isAvailable(storeId)).willReturn(false);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/accidents/region", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .param("startDate", startDate)
                                .param("endDate", endDate)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(UNAVAILABLE_STORE_CODE);
                })
        ;
    }
}
