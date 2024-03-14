package com.ssg.usms.business.store.service;

import com.amazonaws.AmazonClientException;
import com.ssg.usms.business.cctv.repository.CctvRepository;
import com.ssg.usms.business.store.dto.ImageDto;
import com.ssg.usms.business.store.dto.StoreDto;
import com.ssg.usms.business.store.exception.NotExistingBusinessLicenseImgFileKeyException;
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
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoreServiceRetrievingTest {

    private StoreService storeService;
    @Mock
    private StoreRepository mockStoreRepository;
    @Mock
    private CctvRepository mockCctvRepository;
    @Mock
    private ImageRepository mockImageRepository;

    @BeforeEach
    public void setup() {
        storeService = new StoreService(mockStoreRepository, mockCctvRepository, mockImageRepository);
    }

    @DisplayName("[FindAll] : 주어진 조건에 맞게 매장 데이터들을 조회한다.")
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

        given(mockStoreRepository.findAll(anyLong(), isNull(), isNull(), anyLong(), anyInt())).willReturn(stores);

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

    @DisplayName("[findAllByUserId] : 특정 유저가 보유한 매장 데이터들을 조회한다.")
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

        given(mockStoreRepository.findByUserId(any(), anyLong(), anyInt())).willReturn(stores);

        //when
        List<StoreDto> result = storeService.findAllByUserId(1L, 0, 5);

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

    @DisplayName("[FindById] : 고유 키 값에 해당하는 매장 데이터 조회시 해당 매장이 존재하면 매장 데이터를 리턴한다.")
    @Test
    public void testFindByIdWithValidParam() {

        //given
        Long storeId = 1L;
        Long userId = 2L;

        Store store = Store.init(userId,
                "매장명",
                "매장 위치",
                "사업자등록번호",
                "사업자등록증 사본 이미지 키");
        store.setId(storeId);

        given(mockStoreRepository.findById(storeId)).willReturn(store);


        //when
        StoreDto result = storeService.findById(storeId);

        //then
        assertThat(result.getId()).isEqualTo(storeId);
        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @DisplayName("[FindById] : storeId에 해당하는 매장이 없을 경우 예외가 발생한다.")
    @Test
    public void testFindByIdWithNotExistingStore() {

        //given
        Long storeId = 1L;
        Long userId = 2L;

        given(mockStoreRepository.findById(storeId)).willThrow(NotExistingStoreException.class);


        //when & then
        assertThrows(NotExistingStoreException.class,
                () -> storeService.findById(storeId));

    }

    @DisplayName("[findBusinessLicenseImgFile] : 정상적인 요청 값으로 사업자 등록증 사본 이미지를 요청할 경우 이미지를 리턴한다.")
    @Test
    public void testFindBusinessLicenseImgFileWithValidParam() throws IOException {

        //given
        String businessLicenseImgFileKey = "사업자 등록증 사본 이미지 키";

        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        ImageDto imageDto = ImageDto.builder()
                .contentLength(resource.contentLength())
                .content(resource.getInputStream().readAllBytes())
                .build();

        given(mockImageRepository.isExisting(businessLicenseImgFileKey)).willReturn(true);
        given(mockImageRepository.find(businessLicenseImgFileKey)).willReturn(imageDto);


        //when
        ImageDto image = storeService.findBusinessLicenseImgFile(businessLicenseImgFileKey);

        //then
        assertThat(image.getContent()).isEqualTo(resource.getInputStream().readAllBytes());
    }

    @DisplayName("[findBusinessLicenseImgFile] : 사업자 등록증 사본 이미지 키 값이 존재하지 않을 경우 예외가 발생한다.")
    @Test
    public void testFindBusinessLicenseImgFileWithNotExistingBusinessLicenseImgFileKey() {

        //given
        String businessLicenseImgFileKey = "존재하지 않는 사업자 등록증 사본 이미지 키";

        given(mockImageRepository.isExisting(businessLicenseImgFileKey)).willReturn(false);

        //when & then
        assertThrows(NotExistingBusinessLicenseImgFileKeyException.class,
                () -> storeService.findBusinessLicenseImgFile(businessLicenseImgFileKey));

    }

    @DisplayName("[findBusinessLicenseImgFile] : 이미지를 저장소에서 가져오는 과정에서 예외가 발생하는 경우")
    @Test
    public void testFindBusinessLicenseImgFileWithAwsError() {

        //given
        String businessLicenseImgFileKey = "사업자 등록증 사본 이미지 키";

        given(mockImageRepository.isExisting(businessLicenseImgFileKey)).willReturn(true);
        given(mockImageRepository.find(businessLicenseImgFileKey)).willThrow(AmazonClientException.class);

        //when
        assertThrows(AmazonClientException.class,
                () -> storeService.findBusinessLicenseImgFile(businessLicenseImgFileKey));

        verify(mockImageRepository, times(1)).find(any());
    }
}
