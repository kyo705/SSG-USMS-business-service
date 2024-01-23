package com.ssg.usms.business.store.service;

import com.amazonaws.AmazonClientException;
import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.repository.ImageRepository;
import com.ssg.usms.business.store.repository.Store;
import com.ssg.usms.business.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoreServiceUpdatingTest {

    private StoreService storeService;
    @Mock
    private StoreRepository mockStoreRepository;
    @Mock
    private ImageRepository mockiImageRepository;

    @BeforeEach
    public void setup() {
        storeService = new StoreService(mockStoreRepository, mockiImageRepository);
    }

    @DisplayName("[update] : 정상적인 업데이트 요청 파라미터가 전달될 경우 업데이트가 완료된다.")
    @Test
    public void testUpdateWithValidParam() throws IOException {

        //given
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);

        Long storeId = 1L;
        Long userId = 1L;
        String storeName = "매장명";
        String storeAddress = "매장위치";
        String businessLicenseCode = "사업자등록번호";
        String businessLicenseImgId = "사업자등록증 사본 이미지 키";
        InputStream businessLicenseImgFile = resource.getInputStream();

        Store store = Store.init(userId,
                storeName,
                storeAddress,
                businessLicenseCode,
                businessLicenseImgId);
        store.setId(storeId);
        store.setStoreState(StoreState.DISAPPROVAL);

        given(mockStoreRepository.findById(storeId)).willReturn(store);

        //when
        storeService.update(storeId, storeName, storeAddress, storeAddress, businessLicenseImgFile);

        //then
        assertThat(store.getStoreState()).isEqualTo(StoreState.READY);      // 승인 대기 상태로 전환
        verify(mockStoreRepository, times(1)).findById(storeId);
        verify(mockStoreRepository, times(1)).update(store);
        verify(mockiImageRepository, times(1)).save(businessLicenseImgId, businessLicenseImgFile);
    }

    @DisplayName("[update] : 존재하지 않는 매장 id로 요청 파라미터가 전달될 경우 예외가 발생한다.")
    @Test
    public void testUpdateWithNotExistingStore() throws IOException {

        //given
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);

        Long storeId = 1L;
        Long userId = 1L;
        String storeName = "매장명";
        String storeAddress = "매장위치";
        String businessLicenseImgId = "사업자등록증 사본 이미지 키";
        InputStream businessLicenseImgFile = resource.getInputStream();

        given(mockStoreRepository.findById(storeId)).willThrow(new NotExistingStoreException());

        //when
        assertThrows(NotExistingStoreException.class,
                () -> storeService.update(storeId, storeName, storeAddress, storeAddress, businessLicenseImgFile));

        //then
        verify(mockStoreRepository, times(1)).findById(storeId);
        verify(mockStoreRepository, times(0)).update(any());
        verify(mockiImageRepository, times(0)).save(businessLicenseImgId, businessLicenseImgFile);
    }

    @DisplayName("[update] : 이미지 저장 과정에서 예외가 발생하는 상황.")
    @Test
    public void testUpdateWithAwsException() throws IOException {

        //given
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);

        Long storeId = 1L;
        Long userId = 1L;
        String storeName = "매장명";
        String storeAddress = "매장위치";
        String businessLicenseCode = "사업자등록번호";
        String businessLicenseImgId = "사업자등록증 사본 이미지 키";
        InputStream businessLicenseImgFile = resource.getInputStream();

        Store store = Store.init(userId,
                storeName,
                storeAddress,
                businessLicenseCode,
                businessLicenseImgId);
        store.setId(storeId);

        given(mockStoreRepository.findById(storeId)).willReturn(store);
        willThrow(AmazonClientException.class).given(mockiImageRepository).save(any(), any());

        //when
        assertThrows(AmazonClientException.class,
                () -> storeService.update(storeId, storeName, storeAddress, storeAddress, businessLicenseImgFile));

        //then
        verify(mockStoreRepository, times(1)).findById(storeId);
        verify(mockStoreRepository, times(1)).update(store);
        verify(mockiImageRepository, times(1)).save(businessLicenseImgId, businessLicenseImgFile);
    }

    @DisplayName("[changeStoreState] : 존재하는 매장 id로 요청시 성공적으로 매장 상태가 변경된다.")
    @MethodSource("com.ssg.usms.business.store.StoreTestSetup#getStoreStateAndComment")
    @ParameterizedTest
    public void testChangeStoreStateWithValidParam(StoreState state, String message) {

        //given
        Long storeId = 1L;

        Store store = new Store();
        store.setId(storeId);
        store.setUserId(2L);
        store.setStoreName("매장명");
        store.setStoreAddress("매장 주소");
        store.setBusinessLicenseCode("사업자 등록 번호");
        store.setBusinessLicenseImgId("사업자 등록증 사본 이미지 키");
        store.setStoreState(StoreState.READY);

        given(mockStoreRepository.findById(storeId)).willReturn(store);

        //when
        storeService.changeStoreState(storeId, state, message);

        //then
        assertThat(store.getStoreState()).isEqualTo(state);
        assertThat(store.getAdminComment()).isEqualTo(message);
    }

    @DisplayName("[changeStoreState] : 존재하지 않는 매장 id로 요청시 예외가 발생한다.")
    @Test
    public void testChangeStoreStateWithNotExistingStore() {

        //given
        Long storeId = 1L;
        StoreState state = StoreState.APPROVAL;
        String message = "승인 완료";

        given(mockStoreRepository.findById(storeId)).willThrow(NotExistingStoreException.class);

        //when & then
        assertThrows(NotExistingStoreException.class,
                () -> storeService.changeStoreState(storeId, state, message));
    }

}
