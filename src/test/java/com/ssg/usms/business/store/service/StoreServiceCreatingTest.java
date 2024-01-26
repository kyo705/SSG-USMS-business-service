package com.ssg.usms.business.store.service;

import com.amazonaws.AmazonServiceException;
import com.ssg.usms.business.store.dto.StoreDto;
import com.ssg.usms.business.store.repository.ImageRepository;
import com.ssg.usms.business.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoreServiceCreatingTest {

    private StoreService storeService;
    @Mock
    private StoreRepository mockStoreRepository;
    @Mock
    private ImageRepository mockiImageRepository;

    @BeforeEach
    public void setup() {
        storeService = new StoreService(mockStoreRepository, mockiImageRepository);
    }

    @DisplayName("매장 등록 요청이 DB에 저장 후 이미지 파일을 저장한다.")
    @Test
    public void testCreatingStore() throws IOException {

        //given
        StoreDto storeDto = new StoreDto();
        storeDto.setUserId(1L);
        storeDto.setName("매장명");
        storeDto.setAddress("서울 중구 남대문시장10길 2 메사빌딩 21층");
        storeDto.setBusinessLicenseCode("123-45-67890");

        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);

        //when
        storeService.createStore(storeDto, resource.getInputStream(), resource.getFile().length());

        //then
        verify(mockStoreRepository, times(1)).save(any());
        verify(mockiImageRepository, times(1)).save(any(), any(), anyLong());
    }

    @DisplayName("매장 정보를 저장하는 과정에서 예외가 발생할 경우 사업자등록증 사본을 저장하는 로직은 실행되지 않는다.")
    @Test
    public void testCreatingStoreWithADataIntegrityViolationException() {

        //given
        StoreDto storeDto = new StoreDto();
        storeDto.setId(1L);
        storeDto.setUserId(1L);
        storeDto.setName("매장명");
        storeDto.setAddress("서울 중구 남대문시장10길 2 메사빌딩 21층");
        storeDto.setBusinessLicenseCode("123-45-67890");
        BDDMockito.willThrow(DataIntegrityViolationException.class).given(mockStoreRepository).save(any());

        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);

        //when
        assertThrows(DataIntegrityViolationException.class, () -> storeService.createStore(storeDto, resource.getInputStream(), resource.getFile().length()));

        //then
        verify(mockStoreRepository, times(1)).save(any());
        verify(mockiImageRepository, times(0)).save(any(), any(), anyLong());
    }

    @DisplayName("사업자등록증 사본을 저장하는 과정에서 예외가 발생할 경우 해당 예외를 리턴한다.")
    @Test
    public void testCreatingStoreWithAmazonServiceException() {

        //given
        StoreDto storeDto = new StoreDto();
        storeDto.setId(1L);
        storeDto.setUserId(1L);
        storeDto.setName("매장명");
        storeDto.setAddress("서울 중구 남대문시장10길 2 메사빌딩 21층");
        storeDto.setBusinessLicenseCode("123-45-67890");
        BDDMockito.willThrow(AmazonServiceException.class).given(mockiImageRepository).save(any(), any(), anyLong());

        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);

        //when
        assertThrows(AmazonServiceException.class, () -> storeService.createStore(storeDto, resource.getInputStream(), resource.getFile().length()));

        //then
        verify(mockStoreRepository, times(1)).save(any());
        verify(mockiImageRepository, times(1)).save(any(), any(), anyLong());
    }
}
