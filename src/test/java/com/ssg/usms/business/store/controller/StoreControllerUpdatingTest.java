package com.ssg.usms.business.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.error.ErrorResponseDto;
import com.ssg.usms.business.store.dto.HttpRequestCreatingStoreDto;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.ssg.usms.business.constant.CustomStatusCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StoreControllerUpdatingTest {

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

    @DisplayName("[changeStoreState] : 정상적인 파라미터로 요청시 204 상태코드를 리턴한다.")
    @Test
    public void testChangeStoreStateWithValidParam() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;


        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"state\" : 1, \"message\" : \"승인 완료\"}")
                )
                .andExpect(MockMvcResultMatchers.status().is(204));
    }

    @DisplayName("[changeStoreState] : 올바르지 못한 매장 상태 코드로 요청시 400 상태코드를 리턴한다.")
    @Test
    public void testChangeStoreStateWithNotAllowedStoreState() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;


        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"state\" : 6, \"message\" : \"승인 완료\"}")
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_STORE_STATE_FORMAT_CODE);
                })
        ;
    }

    @DisplayName("[changeStoreState] : 존재하지 않은 매장 id로 요청시 400 상태코드를 리턴한다.")
    @Test
    public void testChangeStoreStateWithNotExistingStore() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;

        willThrow(new NotExistingStoreException()).given(storeService).changeStoreState(any(), any(), any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"state\" : 1, \"message\" : \"승인 완료\"}")
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_STORE_CODE);
                })
        ;
    }

    @DisplayName("[updateStore] : 정상적인 파라미터로 요청시 204 상태코드를 리턴한다.")
    @Test
    public void testUpdatingStoreWithValidParam() throws Exception {

        //given
        Long userId = 1L;
        Long storeId = 1L;
        HttpRequestCreatingStoreDto requestParams = new HttpRequestCreatingStoreDto();
        requestParams.setStoreName("매장 명");
        requestParams.setStoreAddress("서울 중구 남대문시장10길 2 메사빌딩 21층");
        requestParams.setBusinessLicenseCode("123-45-67890");

        String fileName = "testImg";
        String contentType = "pdf";
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);

        MockMultipartFile file = new MockMultipartFile(
                "businessLicenseImg", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                resource.getInputStream()
        );

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .file(file)
                                .param("storeName", requestParams.getStoreName())
                                .param("storeAddress", requestParams.getStoreAddress())
                                .param("businessLicenseCode", requestParams.getBusinessLicenseCode())
                )
                .andExpect(MockMvcResultMatchers.status().is(204))
        ;
    }

    @DisplayName("[updateStore] : 존재하지 않은 매장 id로 요청시 예외가 발생한다.")
    @Test
    public void testUpdatingStoreWithNotExistingStore() throws Exception {

        // given
        Long userId = 1L;
        Long storeId = 1L;
        HttpRequestCreatingStoreDto requestParams = new HttpRequestCreatingStoreDto();
        requestParams.setStoreName("매장 명");
        requestParams.setStoreAddress("서울 중구 남대문시장10길 2 메사빌딩 21층");
        requestParams.setBusinessLicenseCode("123-45-67890");

        String fileName = "testImg";
        String contentType = "pdf";
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);

        MockMultipartFile file = new MockMultipartFile(
                "businessLicenseImg", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                resource.getInputStream()
        );


        willThrow(new NotExistingStoreException()).given(storeService).update(any(), any(), any(), any(),any(), any());

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .file(file)
                                .param("storeName", requestParams.getStoreName())
                                .param("storeAddress", requestParams.getStoreAddress())
                                .param("businessLicenseCode", requestParams.getBusinessLicenseCode())
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_EXISTING_STORE_CODE);
                })
        ;
    }

    @DisplayName("[updateStore] : 본인 소유의 매장이 아니면 예외가 발생한다.")
    @Test
    public void testUpdatingStoreWithNotOwnedStore() throws Exception {

        // given
        Long userId = 1L;
        Long storeId = 1L;
        HttpRequestCreatingStoreDto requestParams = new HttpRequestCreatingStoreDto();
        requestParams.setStoreName("매장 명");
        requestParams.setStoreAddress("서울 중구 남대문시장10길 2 메사빌딩 21층");
        requestParams.setBusinessLicenseCode("123-45-67890");

        String fileName = "testImg";
        String contentType = "pdf";
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);

        MockMultipartFile file = new MockMultipartFile(
                "businessLicenseImg", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                resource.getInputStream()
        );


        willThrow(new NotOwnedStoreException()).given(storeService).update(any(), any(), any(), any(),any(), any());

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .file(file)
                                .param("storeName", requestParams.getStoreName())
                                .param("storeAddress", requestParams.getStoreAddress())
                                .param("businessLicenseCode", requestParams.getBusinessLicenseCode())
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_OWNED_STORE_CODE);
                })
        ;
    }

    @DisplayName("[updateStore] : 사업자 등록증 사본을 보내지 않은 경우 예외가 발생한다.")
    @Test
    public void testUpdatingStoreWithEmptyImgFile() throws Exception {

        // given
        Long userId = 1L;
        Long storeId = 1L;
        HttpRequestCreatingStoreDto requestParams = new HttpRequestCreatingStoreDto();
        requestParams.setStoreName("매장 명");
        requestParams.setStoreAddress("서울 중구 남대문시장10길 2 메사빌딩 21층");
        requestParams.setBusinessLicenseCode("123-45-67890");

        String fileName = "testImg";
        String contentType = "pdf";
        InputStream inputStream = null;
        MockMultipartFile file = new MockMultipartFile(
                "businessLicenseImg", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                inputStream
        );

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .file(file)
                                .param("storeName", requestParams.getStoreName())
                                .param("storeAddress", requestParams.getStoreAddress())
                                .param("businessLicenseCode", requestParams.getBusinessLicenseCode())
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(EMPTY_IMG_FILE_CODE);
                })
        ;
    }

    @DisplayName("[updateStore] : 허용되지 않은 파일 확장자명으로 사업자등록증을 등록 요청한 경우 예외가 발생한다.")
    @ValueSource(strings = {"jpg", "png", "mp4", "jpeg"})
    @ParameterizedTest
    public void testUpdatingStoreWithNotAllowedImgFileFormat(String contentType) throws Exception {

        // given
        Long userId = 1L;
        Long storeId = 1L;
        HttpRequestCreatingStoreDto requestParams = new HttpRequestCreatingStoreDto();
        requestParams.setStoreName("매장 명");
        requestParams.setStoreAddress("서울 중구 남대문시장10길 2 메사빌딩 21층");
        requestParams.setBusinessLicenseCode("123-45-67890");

        String fileName = "testImg";
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        MockMultipartFile file = new MockMultipartFile(
                "businessLicenseImg", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                resource.getInputStream()
        );

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .file(file)
                                .param("storeName", requestParams.getStoreName())
                                .param("storeAddress", requestParams.getStoreAddress())
                                .param("businessLicenseCode", requestParams.getBusinessLicenseCode())
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(NOT_ALLOWED_IMG_FILE_FORMAT_CODE);
                })
        ;
    }

    @DisplayName("[updateStore] : 사업자 등록 번호 양식이 잘못된 경우 예외가 발생한다.")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "1234567890", "123-145-67890", "123-56-7890", "123_45_67890"})
    @ParameterizedTest
    public void testUpdatingStoreWithInvalidBusinessLicenseCodeForm(String businessLicenseCode) throws Exception {

        // given
        Long userId = 1L;
        Long storeId = 1L;
        HttpRequestCreatingStoreDto requestParams = new HttpRequestCreatingStoreDto();
        requestParams.setStoreName("매장 명");
        requestParams.setStoreAddress("서울 중구 남대문시장10길 2 메사빌딩 21층");
        requestParams.setBusinessLicenseCode(businessLicenseCode);

        String fileName = "testImg";
        String contentType = "pdf";
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        MockMultipartFile file = new MockMultipartFile(
                "businessLicenseImg", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                resource.getInputStream()
        );

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .file(file)
                                .param("storeName", requestParams.getStoreName())
                                .param("storeAddress", requestParams.getStoreAddress())
                                .param("businessLicenseCode", requestParams.getBusinessLicenseCode())
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(INVALID_BUSINESS_LICENSE_CODE_FORMAT_CODE);
                })
        ;
    }

    @DisplayName("[updateStore] : 허용되지 않은 매장 명 양식으로 등록 요청한 경우 예외가 발생한다.")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "매장명!", " 매장-명", "123456789012345678901" /* 21 자리 수 */})
    @ParameterizedTest
    public void testUpdatingStoreWithInvalidStoreNameForm(String storeName) throws Exception {

        // given
        Long userId = 1L;
        Long storeId = 1L;
        HttpRequestCreatingStoreDto requestParams = new HttpRequestCreatingStoreDto();
        requestParams.setStoreName(storeName);
        requestParams.setStoreAddress("서울 중구 남대문시장10길 2 메사빌딩 21층");
        requestParams.setBusinessLicenseCode("123-45-67890");

        String fileName = "testImg";
        String contentType = "pdf";
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        MockMultipartFile file = new MockMultipartFile(
                "businessLicenseImg", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                resource.getInputStream()
        );

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .file(file)
                                .param("storeName", requestParams.getStoreName())
                                .param("storeAddress", requestParams.getStoreAddress())
                                .param("businessLicenseCode", requestParams.getBusinessLicenseCode())
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(INVALID_STORE_NAME_FORMAT_CODE);
                })
        ;
    }

    @DisplayName("[updateStore] : 허용되지 않은 매장 주소 양식으로 등록 요청한 경우 예외가 발생한다.")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "서울 중구 남대문시장10길 2 메사빌딩 21층 ##", "123456789012345678901234567890123456789012345678901" /* 51 자리 */})
    @ParameterizedTest
    public void testUpdatingStoreWithInvalidStoreAddressForm(String storeAddress) throws Exception {

        // given
        Long userId = 1L;
        Long storeId = 1L;
        HttpRequestCreatingStoreDto requestParams = new HttpRequestCreatingStoreDto();
        requestParams.setStoreName("매장 명");
        requestParams.setStoreAddress(storeAddress);
        requestParams.setBusinessLicenseCode("123-45-67890");

        String fileName = "testImg";
        String contentType = "pdf";
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        MockMultipartFile file = new MockMultipartFile(
                "businessLicenseImg", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                resource.getInputStream()
        );

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart("/api/users/{userId}/stores/{storeId}", userId, storeId)
                                .file(file)
                                .param("storeName", requestParams.getStoreName())
                                .param("storeAddress", requestParams.getStoreAddress())
                                .param("businessLicenseCode", requestParams.getBusinessLicenseCode())
                )
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(result -> {
                    ErrorResponseDto resultBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorResponseDto.class);
                    Assertions.assertThat(resultBody.getCode()).isEqualTo(INVALID_STORE_ADDRESS_FORMAT_CODE);
                })
        ;
    }
}
