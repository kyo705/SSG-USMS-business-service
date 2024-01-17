package com.ssg.usms.business.store.repository;

import com.ssg.usms.business.store.constant.StoreState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Test
    @Transactional
    @DisplayName("JPA 저장 테스트")
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
}
