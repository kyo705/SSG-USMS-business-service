package com.ssg.usms.business.warning.service;


import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.repository.Store;
import com.ssg.usms.business.store.repository.StoreRepository;
import com.ssg.usms.business.warning.RegionWarningTestSetup;
import com.ssg.usms.business.warning.repository.RegionWarning;
import com.ssg.usms.business.warning.repository.RegionWarningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RegionWarningServiceTest {

    private RegionWarningService regionWarningService;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private RegionWarningRepository regionWarningRepository;

    @BeforeEach
    public void setup() {
        regionWarningService = new RegionWarningService(storeRepository, regionWarningRepository);
    }

    @DisplayName("[findByRegion] : 특정 매장 주위의 지역에 해당하는 재난 문자를 조회한다.")
    @Test
    public void testFindByRegion() {

        //given
        Long storeId = 1L;
        String startDate = "2024-01-24";
        String endDate = "2024-01-31";
        int offset = 0;
        int size = 20;

        Store store = new Store();
        store.setId(storeId);
        store.setUserId(1L);
        store.setStoreName("매장 명");
        store.setStoreAddress("서울특별시 강남구 강남대로42길 11 (도곡동)");
        store.setBusinessLicenseCode("123-45-67890");
        store.setBusinessLicenseImgId(UUID.randomUUID().toString());
        store.setStoreState(StoreState.APPROVAL);

        given(storeRepository.findById(storeId)).willReturn(store);

        List<RegionWarning> regionWarningDtoList = RegionWarningTestSetup.getRegionWarning("서울특별시 강남구");

        given(regionWarningRepository.findByRegion("서울특별시 강남구", startDate, endDate, offset, size))
                .willReturn(regionWarningDtoList);

        //when
        regionWarningService.findByRegion(storeId, startDate, endDate, offset, size);

        //then
        verify(storeRepository, times(1)).findById(storeId);
        verify(regionWarningRepository, times(1)).findByRegion("서울특별시 강남구", startDate, endDate, offset, size);

    }

    @DisplayName("[findByRegion] : 날짜 필터링 없이 특정 매장 주위의 지역에 해당하는 재난 문자를 조회한다.")
    @Test
    public void testFindByRegionWithNoDate() {

        //given
        Long storeId = 1L;
        int offset = 0;
        int size = 20;

        Store store = new Store();
        store.setId(storeId);
        store.setUserId(1L);
        store.setStoreName("매장 명");
        store.setStoreAddress("서울특별시 강남구 강남대로42길 11 (도곡동)");
        store.setBusinessLicenseCode("123-45-67890");
        store.setBusinessLicenseImgId(UUID.randomUUID().toString());
        store.setStoreState(StoreState.APPROVAL);

        given(storeRepository.findById(storeId)).willReturn(store);

        List<RegionWarning> regionWarningDtoList = RegionWarningTestSetup.getRegionWarning("서울특별시 강남구");

        given(regionWarningRepository.findByRegion(any(), any(), any(), anyInt(), anyInt()))
                .willReturn(regionWarningDtoList);

        //when
        regionWarningService.findByRegion(storeId, null, null, offset, size);

        //then
        verify(storeRepository, times(1)).findById(storeId);
        verify(regionWarningRepository, times(1)).findByRegion( any(),  any(),  any(),  anyInt(), anyInt());

    }

    @DisplayName("[findByRegion] : 날짜 필터링 없이 특정 매장 주위의 지역에 해당하는 재난 문자를 조회한다.")
    @Test
    public void testFindByRegionWithNotExistingStore() {

        //given
        Long storeId = 1L;
        int offset = 0;
        int size = 20;

        given(storeRepository.findById(storeId)).willThrow(NotExistingStoreException.class);

        //when
        assertThrows(NotExistingStoreException.class, () -> regionWarningService.findByRegion(storeId, null, null, offset, size));

        //then
        verify(storeRepository, times(1)).findById(storeId);
        verify(regionWarningRepository, times(0)).findByRegion( any(),  any(),  any(),  anyInt(), anyInt());

    }
}
