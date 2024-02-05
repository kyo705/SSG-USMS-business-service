package com.ssg.usms.business.store.controller;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.dto.ImageDto;
import com.ssg.usms.business.store.dto.StoreDto;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.exception.NotOwnedBusinessLicenseImgIdException;
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
import org.springframework.core.io.ClassPathResource;
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
import java.util.UUID;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EmbeddedRedis.class)
public class StoreControllerRetrievingTest {

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

    @WithUserDetails("admin")
    @DisplayName("[findStoreById] : 관리자가 정상적인 파라미터로 요청시 매장 정보를 리턴한다.")
    @Test
    public void testFindStoreByIdWithValidParam() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        StoreDto storeDto = new StoreDto();
        storeDto.setId(storeId);
        storeDto.setUserId(userId);
        given(storeService.findById(storeId)).willReturn(storeDto);

        //when & then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/users/{userId}/stores/{storeId}", userId, storeId)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    StoreDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), StoreDto.class);

                    assertThat(responseBody.getUserId()).isEqualTo(userId);
                    assertThat(responseBody.getUserId()).isEqualTo(storeId);
                });

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findStoreById] : 점주 유저가 정상적인 파라미터로 요청시 매장 정보를 리턴한다.")
    @Test
    public void testFindStoreByIdWithValidParam2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        StoreDto storeDto = new StoreDto();
        storeDto.setId(storeId);
        storeDto.setUserId(userId);
        given(storeService.findById(storeId)).willReturn(storeDto);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}", userId, storeId)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    StoreDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), StoreDto.class);

                    assertThat(responseBody.getUserId()).isEqualTo(userId);
                    assertThat(responseBody.getUserId()).isEqualTo(storeId);
                });

    }

    @WithAnonymousUser
    @DisplayName("[findStoreById] : 접근 권한이 없는 유저는 예외가 발생한다.")
    @Test
    public void testFindStoreByIdWithPermissionDeniedUser() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        StoreDto storeDto = new StoreDto();
        storeDto.setId(storeId);
        storeDto.setUserId(userId);
        given(storeService.findById(storeId)).willReturn(storeDto);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}", userId, storeId)
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
        ;

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findStoreById] : 존재하지 않는 매장 정보를 요청할 경우 예외가 발생한다.")
    @Test
    public void testFindStoreByIdWithNotExistingStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        StoreDto storeDto = new StoreDto();
        storeDto.setId(storeId);
        storeDto.setUserId(userId);
        given(storeService.findById(storeId)).willThrow(new NotExistingStoreException());


        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}", userId, storeId)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ErrorResponseDto.class);

                    assertThat(responseBody.getCode()).isEqualTo(NOT_EXISTING_STORE_CODE);
                    assertThat(responseBody.getMessage()).isEqualTo(NOT_EXISTING_STORE_MESSAGE);
                });

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findStoreById] : 존재하지 않는 매장 정보를 요청할 경우 예외가 발생한다.")
    @Test
    public void testFindStoreByIdWithNotExistingStore2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        StoreDto storeDto = new StoreDto();
        storeDto.setId(storeId);
        storeDto.setUserId(userId);
        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(storeId, userId);


        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}", userId, storeId)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ErrorResponseDto.class);

                    assertThat(responseBody.getCode()).isEqualTo(NOT_EXISTING_STORE_CODE);
                    assertThat(responseBody.getMessage()).isEqualTo(NOT_EXISTING_STORE_MESSAGE);
                });

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findStoreById] : 현재 유저가 보유하지 않은 매장 id로 요청한 경우 예외가 발생한다.")
    @Test
    public void testFindStoreByIdWithNotOwnedStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        StoreDto storeDto = new StoreDto();
        storeDto.setId(storeId);
        storeDto.setUserId(userId);
        willThrow(new NotOwnedStoreException()).given(storeService).validateOwnedStore(storeId, userId);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}", userId, storeId)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ErrorResponseDto.class);

                    assertThat(responseBody.getCode()).isEqualTo(NOT_OWNED_STORE_CODE);
                    assertThat(responseBody.getMessage()).isEqualTo(NOT_OWNED_STORE_MESSAGE);
                });

    }

    @WithUserDetails("admin")
    @DisplayName("[findStores] : 현재 유저가 관리자이면 요청 파라미터에 맞게 매장 정보들을 조회한다.")
    @Test
    public void testFindStoreByIdWithAdmin() throws Exception {

        //given
        Long userId = 1L;
        int offset = 0;
        int size = 10;

        StoreDto store1 = new StoreDto();
        store1.setId(1L);
        store1.setUserId(userId);
        store1.setName("무인 매장 1");
        store1.setAddress("서울특별시 강남구 강남대로42길 11");
        store1.setStoreState(StoreState.DISAPPROVAL);
        store1.setAdminComment("사업자 등록증 사본 이미지의 사업자 등록번호와 입력 값이 일치하지 않습니다.");

        StoreDto store2 = new StoreDto();
        store2.setId(2L);
        store2.setUserId(userId);
        store2.setName("무인 매장 2");
        store2.setAddress("서울특별시 강남구 강남대로116길");
        store2.setStoreState(StoreState.DISAPPROVAL);
        store2.setAdminComment("사업자 등록증 사본 이미지가 뚜렷하지 않습니다.");

        StoreDto store3 = new StoreDto();
        store3.setId(3L);
        store3.setUserId(userId);
        store3.setName("무인 매장 2");
        store3.setAddress("서울특별시 강남구 강남대로114길");
        store3.setStoreState(StoreState.DISAPPROVAL);
        store3.setAdminComment("사업자 등록증의 주소와 입력한 주소가 일치하지 않습니다.");

        List<StoreDto> stores = new ArrayList<>();
        stores.add(store1);
        stores.add(store2);
        stores.add(store3);

        given(storeService.findAll(isNull(), isNull(), any(), anyInt(), anyInt()))
                .willReturn(stores);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores", userId)
                                .param("storeState", "2")
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(result -> {
                    List<StoreDto> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), new TypeReference<>(){});

                    assertThat(responseBody.size()).isEqualTo(stores.size());
                    for(StoreDto storeDto : responseBody) {
                        assertThat(storeDto.getUserId()).isEqualTo(userId);
                    }
                })
        ;

        verify(storeService, times(1)).findAll(isNull(), isNull(), any(), anyInt(), anyInt());
        verify(storeService, times(0)).findAllByUserId(any(), anyInt(), anyInt());

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findStores] : 현재 유저가 매장 점주라면 해당 유저의 매장 정보들을 조회한다.")
    @Test
    public void testFindStoreByIdWithStoreOwner() throws Exception {

        //given
        Long userId = 1L;
        int offset = 0;
        int size = 10;

        StoreDto store1 = new StoreDto();
        store1.setId(1L);
        store1.setUserId(userId);

        StoreDto store2 = new StoreDto();
        store2.setId(1L);
        store2.setUserId(userId);

        StoreDto store3 = new StoreDto();
        store3.setId(1L);
        store3.setUserId(userId);

        List<StoreDto> stores = new ArrayList<>();
        stores.add(store1);
        stores.add(store2);
        stores.add(store3);

        given(storeService.findAllByUserId(any(), anyInt(), anyInt()))
                .willReturn(stores);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores", userId)
                                .param("userId", Long.toString(userId))
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(result -> {
                    List<StoreDto> responseBody = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), new TypeReference<>(){});

                    assertThat(responseBody.size()).isEqualTo(stores.size());
                    for(StoreDto storeDto : responseBody) {
                        assertThat(storeDto.getUserId()).isEqualTo(userId);
                    }
                })
        ;

        verify(storeService, times(0)).findAll(any(), isNull(), isNull(), anyInt(), anyInt());
        verify(storeService, times(1)).findAllByUserId(any(), anyInt(), anyInt());

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findStores] : 잘못된 page offset 파라미터가 전달되면 예외가 발생한다.")
    @Test
    public void testFindStoreByIdWithInvalidOffset() throws Exception {

        //given
        Long userId = 1L;
        int offset = -1;
        int size = 10;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores", userId)
                                .param("userId", Long.toString(userId))
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_OFFSET_FORMAT_CODE);
                })
        ;

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findStores] : page offset 파라미터가 전달되지 않으면 예외가 발생한다.")
    @Test
    public void testFindStoreByIdWithInvalidOffset2() throws Exception {

        //given
        Long userId = 1L;
        int size = 10;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores", userId)
                                .param("userId", Long.toString(userId))
                                .param("size", Integer.toString(size))
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_OFFSET_FORMAT_CODE);
                })
        ;

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findStores] : 잘못된 page size 파라미터가 전달되면 예외가 발생한다.")
    @ValueSource(ints = {0,-1})
    @ParameterizedTest
    public void testFindStoreByIdWithInvalidSize(Integer size) throws Exception {

        //given
        Long userId = 1L;
        int offset = 0;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores", userId)
                                .param("userId", Long.toString(userId))
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_SIZE_FORMAT_CODE);
                })
        ;

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findStores] : page size 파라미터가 전달되지 않으면 예외가 발생한다.")
    @Test
    public void testFindStoreByIdWithInvalidSize2() throws Exception {

        //given
        Long userId = 1L;
        int offset = 0;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores", userId)
                                .param("userId", Long.toString(userId))
                                .param("offset", Integer.toString(offset))
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_PAGE_SIZE_FORMAT_CODE);
                })
        ;

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findBusinessLicenseImgFile] : 점주 유저가 정상적인 파라미터로 요청시 이미지 스트림이 전송된다.")
    @Test
    public void testFindBusinessLicenseImgFileWithValidParam() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        String licenseKey = UUID.randomUUID().toString().replace("-", "") + ".jpeg";

        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        ImageDto imageDto = ImageDto.builder()
                .contentLength(resource.contentLength())
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(resource.getInputStream().readAllBytes())
                .build();

        given(storeService.findBusinessLicenseImgFile(licenseKey)).willReturn(imageDto);


        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/license/{licenseKey}", userId, storeId, licenseKey)
                )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.header().string(CONTENT_TYPE, containsString(IMAGE_JPEG_VALUE)))
                .andExpect(result -> {
                    byte[] resultBody = result.getResponse().getContentAsByteArray();
                    Assertions.assertThat(resultBody).isEqualTo(resource.getInputStream().readAllBytes());
                })
        ;

    }

    @WithUserDetails("admin")
    @DisplayName("[findBusinessLicenseImgFile] : 관리자 유저가 정상적인 파라미터로 요청시 이미지 스트림이 전송된다.")
    @Test
    public void testFindBusinessLicenseImgFileWithValidParam2() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        String licenseKey = UUID.randomUUID().toString().replace("-", "") + ".jpeg";

        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        ImageDto imageDto = ImageDto.builder()
                .contentLength(resource.contentLength())
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(resource.getInputStream().readAllBytes())
                .build();

        given(storeService.findBusinessLicenseImgFile(licenseKey)).willReturn(imageDto);


        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/license/{licenseKey}", userId, storeId, licenseKey)
                )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.header().string(CONTENT_TYPE, containsString(IMAGE_JPEG_VALUE)))
                .andExpect(result -> {
                    byte[] resultBody = result.getResponse().getContentAsByteArray();
                    Assertions.assertThat(resultBody).isEqualTo(resource.getInputStream().readAllBytes());
                })
        ;

    }

    @WithAnonymousUser
    @DisplayName("[findBusinessLicenseImgFile] : 접근 권한 없는 유저인 경우 예외가 발생한다.")
    @Test
    public void testFindBusinessLicenseImgFileWithPermissionDeniedUser() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        String licenseKey = UUID.randomUUID().toString().replace("-", "") + ".jpeg";

        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        ImageDto imageDto = ImageDto.builder()
                .contentLength(resource.contentLength())
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(resource.getInputStream().readAllBytes())
                .build();
        given(storeService.findBusinessLicenseImgFile(licenseKey)).willReturn(imageDto);


        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/license/{licenseKey}", userId, storeId, licenseKey)
                )
                .andExpect(MockMvcResultMatchers.status().is(403))
        ;

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findBusinessLicenseImgFile] : 잘못된 사업자 등록증 사본 이미지 키로 요청시 예외가 발생한다.")
    @Test
    public void testFindBusinessLicenseImgFileWithInvalidLicenseKey() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        String licenseKey = "123-45-67890";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/license/{licenseKey}", userId, storeId, licenseKey)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(INVALID_BUSINESS_LICENSE_IMG_KEY_FORMAT_CODE);
                })
        ;

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findBusinessLicenseImgFile] : 존재하지 않는 매장 id로 요청시 예외가 발생한다.")
    @Test
    public void testFindBusinessLicenseImgFileWithNotExistingStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        String licenseKey = UUID.randomUUID().toString().replace("-", "") + ".jpeg";

        willThrow(new NotExistingStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/license/{licenseKey}", userId, storeId, licenseKey)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_STORE_CODE);
                })
        ;

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findBusinessLicenseImgFile] : 본인 소유가 아닌 매장 id로 요청시 예외가 발생한다.")
    @Test
    public void testFindBusinessLicenseImgFileWithNotOwnedStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        String licenseKey = UUID.randomUUID().toString().replace("-", "") + ".jpeg";

        willThrow(new NotOwnedStoreException()).given(storeService).validateOwnedStore(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/license/{licenseKey}", userId, storeId, licenseKey)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_STORE_CODE);
                })
        ;

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findBusinessLicenseImgFile] : 본인 소유가 아닌 사업자 등록증 사본 이미지 키로 요청시 예외가 발생한다.")
    @Test
    public void testFindBusinessLicenseImgFileWithNotOwnedBusinessLicenseImgKey() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        String licenseKey = UUID.randomUUID().toString().replace("-", "") + ".jpeg";

        willThrow(new NotOwnedBusinessLicenseImgIdException())
                .given(storeService)
                .validateOwnedBusinessLicenseImgKey(any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/license/{licenseKey}", userId, storeId, licenseKey)
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_BUSINESS_LICENSE_IMG_KEY_CODE);
                })
        ;

    }

    @WithUserDetails("storeOwner")
    @DisplayName("[findBusinessLicenseImgFile] : 저장 과정에서 예외가 발생하는 경우")
    @Test
    public void testFindBusinessLicenseImgFileWithS3Error() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        String licenseKey = UUID.randomUUID().toString().replace("-", "") + ".jpeg";

        given(storeService.findBusinessLicenseImgFile(any())).willThrow(new AmazonClientException("예외발생"));

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/users/{userId}/stores/{storeId}/license/{licenseKey}", userId, storeId, licenseKey)
                )
                .andExpect(MockMvcResultMatchers.status().is(500))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(500);
                })
        ;

    }
}
