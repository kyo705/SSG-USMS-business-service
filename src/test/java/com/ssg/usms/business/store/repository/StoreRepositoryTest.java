package com.ssg.usms.business.store.repository;

import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.dto.HttpRequestRetrievingStoreDto;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional(readOnly = true)
@ActiveProfiles("test")
@SpringBootTest
public class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @DisplayName("Store 저장 테스트")
    @Test
    public void testSave() {

        //given
        Store store = new Store();
        store.setUserId(1L);
        store.setStoreName("매장명");
        store.setStoreAddress("서울 중구 남대문시장10길 2 메사빌딩 21층");
        store.setBusinessLicenseCode("123-45-67890");
        store.setBusinessLicenseImgId(UUID.randomUUID().toString().replace("-", ""));
        store.setStoreState(StoreState.READY);

        assertThat(store.getId()).isNull();

        //when
        Store savedStore = storeRepository.save(store);

        //then
        assertThat(store.getId()).isNotNull();
        assertThat(savedStore.getId()).isNotNull();
        assertThat(store).isEqualTo(savedStore);
    }

    @DisplayName("Store 전체 조회 테스트")
    @MethodSource("com.ssg.usms.business.store.StoreTestSetup#getHttpRequestRetrievingStoreDto")
    @ParameterizedTest
    public void testFindAll(HttpRequestRetrievingStoreDto requestParam) {

        //given

        //when
        List<Store> stores = storeRepository.findAll(requestParam.getUserId(),
                                                    requestParam.getBusinessLicenseCode(),
                                                    requestParam.getStoreState(),
                                                    requestParam.getOffset(),
                                                    requestParam.getSize());

        //then
        assertThat(stores.size()).isLessThanOrEqualTo(requestParam.getSize());
        for(Store store : stores) {
            if(requestParam.getUserId() != null) {
                assertThat(store.getUserId()).isEqualTo(requestParam.getUserId());
            }
            if(requestParam.getBusinessLicenseCode() != null) {
                assertThat(store.getBusinessLicenseCode()).isEqualTo(requestParam.getBusinessLicenseCode());
            }
            if(requestParam.getStoreState() != null) {
                assertThat(store.getStoreState()).isEqualTo(requestParam.getStoreState());
            }
        }
    }

    @DisplayName("특정 유저 ID로 조회 테스트")
    @Test
    public void testFindByUserId() {

        //given
        Long userId = 1L;
        int offset = 0;
        int size = 20;

        //when
        List<Store> stores = storeRepository.findByUserId(userId, offset, size);

        //then
        assertThat(stores.size()).isLessThanOrEqualTo(size);
        for(Store store : stores) {
            assertThat(store.getUserId()).isEqualTo(userId);
        }
    }

    @DisplayName("pk로 조회 테스트")
    @Test
    public void testFindById() {

        //given
        Long storeId = 1L;

        //when
        Store store = storeRepository.findById(storeId);

        //then
        assertThat(store.getId()).isEqualTo(storeId);
    }

    @DisplayName("업데이트 테스트")
    @Test
    public void testUpdate() {

        //given
        Long storeId = 1L;
        String updatingStoreName = "업데이트된 매장명";
        Store store = storeRepository.findById(storeId);
        store.setStoreName(updatingStoreName);

        //when
        storeRepository.update(store);

        //then
        Store updatedStore = storeRepository.findById(storeId);
        assertThat(updatedStore.getStoreName()).isEqualTo(updatingStoreName);
    }

    @DisplayName("삭제 테스트")
    @Test
    public void testDelete() {

        //given
        Long storeId = 1L;
        String updatingStoreName = "업데이트된 매장명";
        Store store = storeRepository.findById(storeId);
        store.setStoreName(updatingStoreName);

        //when
        storeRepository.delete(store);

        //then
        assertThrows(NotExistingStoreException.class, () -> storeRepository.findById(storeId));

    }
}
