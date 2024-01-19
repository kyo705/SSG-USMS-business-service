package com.ssg.usms.business.store.service;

import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.dto.StoreDto;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.exception.NotMatchingStoreAndBusinessLicenseImgIdException;
import com.ssg.usms.business.store.exception.NotOwnedStoreException;
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
import java.util.stream.Collectors;

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

    @Transactional
    public StoreDto findById(Long storeId, Long userId) {

        Store store = storeRepository.findById(storeId);
        if(store == null) {
            throw new NotExistingStoreException();
        }
        if(store.getUserId() != userId) {
            throw new NotOwnedStoreException();
        }
        return new StoreDto(store);
    }

    @Transactional
    public byte[] findBusinessLicenseImgFile(Long storeId, Long userId, String businessLicenseImgFileKey) {

        Store store = storeRepository.findById(storeId);
        if(store == null) {
            throw new NotExistingStoreException();
        }
        if(store.getUserId() != userId) {
            throw new NotOwnedStoreException();
        }
        if(!store.getBusinessLicenseImgId().equals(businessLicenseImgFileKey)) {
            throw new NotMatchingStoreAndBusinessLicenseImgIdException();
        }
        return imageRepository.find(businessLicenseImgFileKey);
    }

    @Transactional
    public List<StoreDto> findAllByUserId(Long userId, int offset, int size) {

        return storeRepository.findByUserId(userId, offset, size)
                .stream()
                .map(StoreDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<StoreDto> findAll(Long userId, String businessLicenseCode, StoreState storeState, int offset, int size) {

        return storeRepository.findAll(userId, businessLicenseCode, storeState, offset, size)
                .stream()
                .map(StoreDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long storeId, Long userId, String storeName, String storeAddress, String businessLicenseCode, InputStream businessLicenseImgFile) {

        Store store = storeRepository.findById(storeId);
        if(store == null) {
            throw new NotExistingStoreException();
        }
        if(store.getUserId() != userId) {
            throw new NotOwnedStoreException();
        }
        store.update(storeName, storeAddress, businessLicenseCode);
        storeRepository.update(store);

        imageRepository.save(store.getBusinessLicenseImgId(), businessLicenseImgFile);
    }

    @Transactional
    public void changeStoreState(Long storeId, StoreState requestState, String message) {

        Store store = storeRepository.findById(storeId);
        if(store == null) {
            throw new NotExistingStoreException();
        }
        if(requestState == StoreState.APPROVAL){
            store.approve(message);
        }
        else if(requestState == StoreState.DISAPPROVAL){
            store.disapprove(message);
        }
        else if(requestState == StoreState.STOPPED) {
            store.lock(message);
        }
    }

    @Transactional
    public void delete(Long storeId, Long userId) {

        Store store = storeRepository.findById(storeId);
        if(store == null) {
            throw new NotExistingStoreException();
        }
        if(store.getUserId() != userId) {
            throw new NotOwnedStoreException();
        }
        storeRepository.delete(store);
    }
}
