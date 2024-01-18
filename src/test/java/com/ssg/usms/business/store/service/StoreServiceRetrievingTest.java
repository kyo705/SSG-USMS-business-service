package com.ssg.usms.business.store.service;

import com.ssg.usms.business.store.dto.StoreDto;
import com.ssg.usms.business.store.repository.ImageRepository;
import com.ssg.usms.business.store.repository.Store;
import com.ssg.usms.business.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StoreServiceRetrievingTest {

    private StoreService storeService;
    @Mock
    private StoreRepository mockStoreRepository;
    @Mock
    private ImageRepository mockiImageRepository;

    @BeforeEach
    public void setup() {
        storeService = new StoreService(mockStoreRepository, mockiImageRepository);
    }

    @DisplayName("주어진 조건에 맞게 매장 데이터들을 조회한다.")
    @Test
    public void testFindAll() {

        //given
        List<Store> stores = new ArrayList<>();
        Store store1 = Store.init(1L, "매장명1", "매장 주소1", "사업자등록번호", "사업자등록증 사본 이미지 키");
        Store store2 = Store.init(1L, "매장명2", "매장 주소2", "사업자등록번호", "사업자등록증 사본 이미지 키");
        Store store3 = Store.init(1L, "매장명3", "매장 주소3", "사업자등록번호", "사업자등록증 사본 이미지 키");
        Store store4 = Store.init(1L, "매장명4", "매장 주소4", "사업자등록번호", "사업자등록증 사본 이미지 키");
        stores.add(store1);
        stores.add(store2);
        stores.add(store3);
        stores.add(store4);

        given(mockStoreRepository.findAll(anyLong(), isNull(), isNull(), anyInt(), anyInt())).willReturn(stores);

        //when
        List<StoreDto> result = storeService.findAll(1L, null, null, 0, 5);

        //then
        assertThat(result.size()).isEqualTo(stores.size());
        for(int i=0;i<result.size();i++) {
            assertThat(result.get(i).getName()).isEqualTo(stores.get(i).getStoreName());
            assertThat(result.get(i).getAddress()).isEqualTo(stores.get(i).getStoreAddress());
            assertThat(result.get(i).getBusinessLicenseImgId()).isEqualTo(stores.get(i).getBusinessLicenseImgId());
            assertThat(result.get(i).getBusinessLicenseCode()).isEqualTo(stores.get(i).getBusinessLicenseCode());
            assertThat(result.get(i).getStoreState()).isEqualTo(stores.get(i).getStoreState());
        }
    }

    @DisplayName("특정 유저가 보유한 매장 데이터들을 조회한다.")
    @Test
    public void testFindByUserId() {

        //given
        List<Store> stores = new ArrayList<>();
        Store store1 = Store.init(1L, "매장명1", "매장 주소1", "사업자등록번호", "사업자등록증 사본 이미지 키");
        Store store2 = Store.init(1L, "매장명2", "매장 주소2", "사업자등록번호", "사업자등록증 사본 이미지 키");
        Store store3 = Store.init(1L, "매장명3", "매장 주소3", "사업자등록번호", "사업자등록증 사본 이미지 키");
        Store store4 = Store.init(1L, "매장명4", "매장 주소4", "사업자등록번호", "사업자등록증 사본 이미지 키");
        stores.add(store1);
        stores.add(store2);
        stores.add(store3);
        stores.add(store4);

        given(mockStoreRepository.findByUserId(any(), anyInt(), anyInt())).willReturn(stores);

        //when
        List<StoreDto> result = storeService.findByUserId(1L, 0, 5);

        //then
        assertThat(result.size()).isEqualTo(stores.size());
        for(int i=0;i<result.size();i++) {
            assertThat(result.get(i).getName()).isEqualTo(stores.get(i).getStoreName());
            assertThat(result.get(i).getAddress()).isEqualTo(stores.get(i).getStoreAddress());
            assertThat(result.get(i).getBusinessLicenseImgId()).isEqualTo(stores.get(i).getBusinessLicenseImgId());
            assertThat(result.get(i).getBusinessLicenseCode()).isEqualTo(stores.get(i).getBusinessLicenseCode());
            assertThat(result.get(i).getStoreState()).isEqualTo(stores.get(i).getStoreState());
        }
    }
}
