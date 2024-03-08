package com.ssg.usms.business.store.service;

import com.ssg.usms.business.cctv.repository.Cctv;
import com.ssg.usms.business.cctv.repository.CctvRepository;
import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.dto.ImageDto;
import com.ssg.usms.business.store.dto.StoreDto;
import com.ssg.usms.business.store.exception.NotExistingBusinessLicenseImgFileKeyException;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import com.ssg.usms.business.store.exception.NotOwnedBusinessLicenseImgIdException;
import com.ssg.usms.business.store.exception.NotOwnedStoreException;
import com.ssg.usms.business.store.repository.ImageRepository;
import com.ssg.usms.business.store.repository.Store;
import com.ssg.usms.business.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final CctvRepository cctvRepository;
    private final ImageRepository imageRepository;


    @Transactional
    public StoreDto createStore(StoreDto storeDto, MultipartFile businessLicenseImgFile) {

        // 매장 메타데이터 저장
        String fileFormat = businessLicenseImgFile.getOriginalFilename().split("[.]")[1];
        String businessLicenseImgId = UUID.randomUUID().toString().replace("-", "") + "." + fileFormat;

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

        return new StoreDto(store);
    }

    @Transactional
    public StoreDto findById(Long storeId) {

        return new StoreDto(storeRepository.findById(storeId));
    }

    @Transactional
    public ImageDto findBusinessLicenseImgFile(String businessLicenseImgFileKey) {

        if(!imageRepository.isExisting(businessLicenseImgFileKey)) {
            throw new NotExistingBusinessLicenseImgFileKeyException();
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

    @Transactional(readOnly = true)
    public boolean isAvailable(Long storeId) {

        return storeRepository.findById(storeId).getStoreState() == StoreState.APPROVAL;
    }

    @Transactional
    public void update(Long storeId,
                       String storeName,
                       String storeAddress,
                       String businessLicenseCode,
                       MultipartFile businessLicenseImgFile) {


        Store store = storeRepository.findById(storeId);
        store.update(storeName, storeAddress, businessLicenseCode);
        storeRepository.update(store);

        imageRepository.save(store.getBusinessLicenseImgId(), businessLicenseImgFile);
    }

    @Transactional
    public void changeStoreState(Long storeId, StoreState requestState, String message) {

        Store store = storeRepository.findById(storeId);
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
    public void delete(Long storeId) {

        // 매장 메타데이터 삭제
        Store store = storeRepository.findById(storeId);
        storeRepository.delete(store);

        // 매장 내 모든 cctv 만료시킴
        int offset = 0;
        int size = 100;
        while(true) {
            List<Cctv> cctvs = cctvRepository.findByStoreId(storeId, offset, size);
            for(Cctv cctv : cctvs) {
                cctv.expire();
            }
            if(cctvs.size() != size){
                break;
            }
            offset++;
        }
    }


    @Transactional(readOnly = true)
    public void validateStore(Long storeId) {
        Store store = storeRepository.findById(storeId);
        if(store == null) {
            throw new NotExistingStoreException();
        }
    }

    @Transactional(readOnly = true)
    public void validateOwnedStore(Long storeId, Long userId) {

        validateStore(storeId);

        Store store = storeRepository.findById(storeId);
        if(store.getUserId() != userId) {
            throw new NotOwnedStoreException();
        }
    }

    @Transactional(readOnly = true)
    public void validateOwnedBusinessLicenseImgKey(Long storeId, String businessLicenseImgFileKey) {

        validateStore(storeId);

        Store store = storeRepository.findById(storeId);
        if(!store.getBusinessLicenseImgId().equals(businessLicenseImgFileKey)) {
            throw new NotOwnedBusinessLicenseImgIdException();
        }
    }
}
