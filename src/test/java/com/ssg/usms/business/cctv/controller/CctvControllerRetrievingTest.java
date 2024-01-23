package com.ssg.usms.business.cctv.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.cctv.CctvTestSetup;
import com.ssg.usms.business.cctv.dto.CctvDto;
import com.ssg.usms.business.cctv.exception.NotExistingCctvException;
import com.ssg.usms.business.cctv.exception.NotOwnedCctvException;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest
public class CctvControllerRetrievingTest {

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

    @DisplayName("[findById] : 점주 유저로 자신의 매장 cctv를 요청하는 경우 200 상태코드를 리턴한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindByIdWithStoreOwner() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        CctvDto cctvDto = new CctvDto();
        cctvDto.setId(cctvId);
        cctvDto.setCctvName("cctv 별칭");
        cctvDto.setStoreId(storeId);
        cctvDto.setCctvStreamKey("스트림키");
        cctvDto.setExpired(false);
        cctvDto.setIsConnected(false);

        given(cctvService.findById(cctvId)).willReturn(cctvDto);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    CctvDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), CctvDto.class);
                    assertThat(responseBody.getId()).isEqualTo(cctvDto.getId());
                    assertThat(responseBody.getStoreId()).isEqualTo(cctvDto.getStoreId());
                    assertThat(responseBody.getCctvName()).isEqualTo(cctvDto.getCctvName());
                    assertThat(responseBody.getCctvStreamKey()).isEqualTo(cctvDto.getCctvStreamKey());
                    assertThat(responseBody.isExpired()).isEqualTo(cctvDto.isExpired());
                    assertThat(responseBody.getIsConnected()).isEqualTo(cctvDto.getIsConnected());
                })
        ;

        verify(storeService, times(1)).validateOwnedStore(any(), any());
        verify(cctvService, times(1)).validateOwnedCctv(any(), any());
    }

    @DisplayName("[findById] : 관리자 유저로 특정 매장 cctv를 요청하는 경우 200 상태코드를 리턴한다.")
    @WithUserDetails("admin")
    @Test
    public void testFindByIdWithAdmin() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        CctvDto cctvDto = new CctvDto();
        cctvDto.setId(cctvId);
        cctvDto.setCctvName("cctv 별칭");
        cctvDto.setStoreId(storeId);
        cctvDto.setCctvStreamKey("스트림키");
        cctvDto.setExpired(false);
        cctvDto.setIsConnected(false);

        given(cctvService.findById(cctvId)).willReturn(cctvDto);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    CctvDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), CctvDto.class);
                    assertThat(responseBody.getId()).isEqualTo(cctvDto.getId());
                    assertThat(responseBody.getStoreId()).isEqualTo(cctvDto.getStoreId());
                    assertThat(responseBody.getCctvName()).isEqualTo(cctvDto.getCctvName());
                    assertThat(responseBody.getCctvStreamKey()).isEqualTo(cctvDto.getCctvStreamKey());
                    assertThat(responseBody.isExpired()).isEqualTo(cctvDto.isExpired());
                    assertThat(responseBody.getIsConnected()).isEqualTo(cctvDto.getIsConnected());
                })
        ;

        verify(storeService, times(0)).validateOwnedStore(any(), any());
        verify(cctvService, times(0)).validateOwnedCctv(any(), any());
    }

    @DisplayName("[findById] : 접근 권한 없는 유저로 요청시 403 상태코드를 리턴한다.")
    @WithAnonymousUser
    @Test
    public void testFindByIdWithPermissionDeniedUser() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
        ;

        verify(storeService, times(0)).validateOwnedStore(any(), any());
        verify(cctvService, times(0)).validateOwnedCctv(any(), any());
    }

    @DisplayName("[findById] : 존재하지 않는 storeId로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindByIdWithNotExistingStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_STORE_CODE);
                })
        ;
    }

    @DisplayName("[findById] : 본인 소유가 아닌 storeId로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindByIdWithNotOwnedStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        willThrow(new NotOwnedStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_STORE_CODE);
                })
        ;
    }

    @DisplayName("[findById] : 존재하지 않는 CCTV일 경우 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindByIdWithNotExistingCctv() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        willThrow(new NotExistingCctvException()).given(cctvService).validateOwnedCctv(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_CCTV_CODE);
                })
        ;
    }

    @DisplayName("[findById] : 본인 소유가 아닌 CCTV를 요청할 경우 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindByIdWithNotOwnedCctv() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        Long cctvId = 1L;

        willThrow(new NotOwnedCctvException()).given(cctvService).validateOwnedCctv(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}", userId, storeId, cctvId)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_CCTV_CODE);
                })
        ;
    }

    @DisplayName("[findAllByStoreId] : 점주 유저로 특정 매장의 cctv들을 조회하는 경우 200 상태코드를 리턴한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindAllByStoreIdWithStoreOwner() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 10;

        List<CctvDto> cctvList = CctvTestSetup.getCctvList(storeId)
                .stream()
                .map(CctvDto::new)
                .collect(Collectors.toList());

        given(cctvService.findAllByStoreId(storeId, offset, size)).willReturn(cctvList);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs", userId, storeId)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    List<CctvDto> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8),  new TypeReference<>() {});

                    assertThat(responseBody.size()).isEqualTo(cctvList.size());
                    for(CctvDto cctv : responseBody) {
                        assertThat(cctv.getStoreId()).isEqualTo(storeId);
                    }
                })
        ;

        verify(storeService, times(1)).validateOwnedStore(any(), any());
    }

    @DisplayName("[findAllByStoreId] : 관리자 유저로 특정 매장의 cctv들을 조회하는 경우 200 상태코드를 리턴한다.")
    @WithUserDetails("admin")
    @Test
    public void testFindAllByStoreIdWithAdmin() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 10;

        List<CctvDto> cctvList = CctvTestSetup.getCctvList(storeId)
                .stream()
                .map(CctvDto::new)
                .collect(Collectors.toList());

        given(cctvService.findAllByStoreId(storeId, offset, size)).willReturn(cctvList);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs", userId, storeId)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    List<CctvDto> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8),  new TypeReference<>() {});

                    assertThat(responseBody.size()).isEqualTo(cctvList.size());
                    for(CctvDto cctv : responseBody) {
                        assertThat(cctv.getStoreId()).isEqualTo(storeId);
                    }
                })
        ;

        verify(storeService, times(0)).validateOwnedStore(any(), any());
    }

    @DisplayName("[findAllByStoreId] : 접근 권한 없는 유저로 요청시 403 상태코드를 리턴한다.")
    @WithAnonymousUser
    @Test
    public void testFindAllByStoreIdWithPermissionDeniedUser() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 10;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs", userId, storeId)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
        ;

        verify(storeService, times(0)).validateOwnedStore(any(), any());
    }

    @DisplayName("[findAllByStoreId] : 잘못된 offset으로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindAllByStoreIdWithInvalidOffset() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = -1;
        int size = 10;

        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs", userId, storeId)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_OFFSET_FORMAT_CODE);
                })
        ;
        verify(storeService, times(0)).validateOwnedStore(any(), any());
    }

    @DisplayName("[findAllByStoreId] : offset 없이 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindAllByStoreIdWithInvalidOffset2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int size = 10;

        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs", userId, storeId)
                                .param("size", Integer.toString(size))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_OFFSET_FORMAT_CODE);
                })
        ;
        verify(storeService, times(0)).validateOwnedStore(any(), any());
    }

    @DisplayName("[findAllByStoreId] : 잘못된 size로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @ValueSource(ints = {0, -1, -15})
    @ParameterizedTest
    public void testFindAllByStoreIdWithInvalidSize(int size) throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;

        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs", userId, storeId)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_SIZE_FORMAT_CODE);
                })
        ;
        verify(storeService, times(0)).validateOwnedStore(any(), any());
    }

    @DisplayName("[findAllByStoreId] : size 없이 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindAllByStoreIdWithInvalidSize2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;

        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs", userId, storeId)
                                .param("offset", Integer.toString(offset))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_SIZE_FORMAT_CODE);
                })
        ;
        verify(storeService, times(0)).validateOwnedStore(any(), any());
    }

    @DisplayName("[findAllByStoreId] : 존재하지 않는 storeId로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindAllByStoreIdWithNotExistingStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 10;

        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs", userId, storeId)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_STORE_CODE);
                })
        ;
        verify(storeService, times(1)).validateOwnedStore(any(), any());
    }

    @DisplayName("[findAllByStoreId] : 본인 소유가 아닌 storeId로 요청시 예외가 발생한다.")
    @WithUserDetails("storeOwner")
    @Test
    public void testFindAllByStoreIdWithNotOwnedStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int size = 10;

        willThrow(new NotOwnedStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/cctvs", userId, storeId)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_STORE_CODE);
                })
        ;
        verify(storeService, times(1)).validateOwnedStore(any(), any());
    }

}
