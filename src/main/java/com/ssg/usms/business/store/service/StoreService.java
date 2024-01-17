package com.ssg.usms.business.store.service;

import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.dto.StoreDto;
import com.ssg.usms.business.store.repository.ImageRepository;
import com.ssg.usms.business.store.repository.Store;
import com.ssg.usms.business.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final ImageRepository imageRepository;

    public List<StoreDto> getStoresByUsername(String username) {

        return null;
    }

    @Transactional
    public void createStore(StoreDto storeDto, InputStream businessLicenseImgFile) {

        // 매장 메타데이터 저장
        String businessLicenseImgId = UUID.randomUUID().toString().replace("-", "");

        Store store = new Store();
        store.setUserId(storeDto.getUserId());
        store.setStoreName(storeDto.getName());
        store.setStoreAddress(storeDto.getAddress());
        store.setBusinessLicenseCode(storeDto.getBusinessLicenseCode());
        store.setBusinessLicenseImgId(businessLicenseImgId);
        store.setStoreState(StoreState.READY);

        storeRepository.save(store);

        // 이미지 파일 저장
        imageRepository.save(businessLicenseImgId, businessLicenseImgFile);

    }
}
