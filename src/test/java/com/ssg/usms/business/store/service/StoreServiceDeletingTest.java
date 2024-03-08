package com.ssg.usms.business.store.service;

import com.ssg.usms.business.cctv.repository.CctvRepository;
import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.repository.ImageRepository;
import com.ssg.usms.business.store.repository.Store;
import com.ssg.usms.business.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoreServiceDeletingTest {

    private StoreService storeService;
    @Mock
    private StoreRepository mockStoreRepository;
    @Mock
    private CctvRepository mockCctvRepository;
    @Mock
    private ImageRepository mockiImageRepository;

    @BeforeEach
    public void setup() {
        storeService = new StoreService(mockStoreRepository, mockCctvRepository, mockiImageRepository);
    }

    @DisplayName("정상적인 매장 삭제 요청시 성공한다.")
    @Test
    public void testDeletingWithValidParam() {

        //given
        Long storeId = 1L;
        Long userId = 1L;
        Store store = new Store();
        store.setId(storeId);
        store.setUserId(userId);
        store.setStoreName("매장명");
        store.setStoreAddress("매장 주소");
        store.setBusinessLicenseCode("사업자 등록 번호");
        store.setBusinessLicenseImgId("사업자 등록증 사본 이미지 키");
        store.setStoreState(StoreState.READY);

        given(mockStoreRepository.findById(storeId)).willReturn(store);

        //when
        storeService.delete(storeId);

        //then
        verify(mockStoreRepository, times(1)).findById(storeId);
        verify(mockStoreRepository, times(1)).delete(store);

    }

    @DisplayName("존재하지 않는 매장 id로 매장 삭제 요청시 예외가 발생한다.")
    @Test
    public void testDeletingWithNotExistingStore() {

        //given
        Long storeId = 1L;
        Long userId = 1L;

        given(mockStoreRepository.findById(storeId)).willThrow(NotExistingStoreException.class);

        //when & then
        assertThrows(NotExistingStoreException.class, () -> storeService.delete(storeId));

        verify(mockStoreRepository, times(1)).findById(storeId);
        verify(mockCctvRepository, times(0)).findByStoreId(anyLong(), anyInt(), anyInt());
        verify(mockStoreRepository, times(0)).delete(any());

    }
}
