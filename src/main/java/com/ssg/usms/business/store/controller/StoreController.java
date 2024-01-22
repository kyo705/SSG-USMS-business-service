package com.ssg.usms.business.store.controller;

import com.ssg.usms.business.store.annotation.BusinessLicenseImgKey;
import com.ssg.usms.business.store.dto.HttpRequestChangingStoreStateDto;
import com.ssg.usms.business.store.dto.HttpRequestCreatingStoreDto;
import com.ssg.usms.business.store.dto.HttpRequestRetrievingStoreDto;
import com.ssg.usms.business.store.dto.StoreDto;
import com.ssg.usms.business.store.exception.EmptyImgFileException;
import com.ssg.usms.business.store.exception.NotAllowedImgFileFormatException;
import com.ssg.usms.business.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.ssg.usms.business.store.constant.StoreConstants.ALLOWED_IMG_FILE_FORMATS;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/api/users/{userId}/stores")
    public ResponseEntity<Void> createStore(@PathVariable Long userId,
                                            @ModelAttribute @Valid HttpRequestCreatingStoreDto requestParam,
                                            @RequestPart(name = "businessLicenseImg") MultipartFile businessLicenseImgFile)
            throws IOException {

        // 로그인된 모든 유저가 접근 가능

        //multipartFile 포맷 확인
        if(businessLicenseImgFile.isEmpty()) {
            throw new EmptyImgFileException();
        }
        String fileFormat = businessLicenseImgFile.getOriginalFilename().split("[.]")[1];
        if(!ALLOWED_IMG_FILE_FORMATS.contains(fileFormat)) {
            throw new NotAllowedImgFileFormatException();
        }

        StoreDto store = StoreDto.builder()
                .userId(userId)
                .name(requestParam.getStoreName())
                .address(requestParam.getStoreAddress())
                .businessLicenseCode(requestParam.getBusinessLicenseCode())
                .build();

       storeService.createStore(store, businessLicenseImgFile.getInputStream());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/users/{userId}/stores")
    public List<StoreDto> findStores(@PathVariable Long userId,
                                     @ModelAttribute @Valid HttpRequestRetrievingStoreDto requestParam)
            throws IllegalAccessException {

        // 현재 세션의 권한에 따라 아래 실행할 서비스 달라짐 ( 1. 관리자일 경우, 2. 점주 고객일 경우 )
        List<? extends GrantedAuthority> authorities = (List<? extends GrantedAuthority>) SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities();

        if (authorities.size() != 1) {
            throw new IllegalStateException("유저 권한 갯수가 현재 이상함");
        }

        if (authorities.get(0).getAuthority().equals("ROLE_ADMIN")) {
            return storeService.findAll(requestParam.getUserId(),
                    requestParam.getBusinessLicenseCode(),
                    requestParam.getStoreState(),
                    requestParam.getOffset(),
                    requestParam.getSize());
        }
        if (authorities.get(0).getAuthority().equals("ROLE_STORE_OWNER")) {
            return storeService.findAllByUserId(userId, requestParam.getOffset(), requestParam.getSize());
        }
        throw new IllegalAccessException("접근 권한이 없는 유저입니다.");
    }

    @GetMapping("/api/users/{userId}/stores/{storeId}")
    public StoreDto findStoreById(@PathVariable Long userId,
                                  @PathVariable Long storeId) {

        // 로그인된 모든 유저가 접근 가능

        return storeService.findById(storeId, userId);
    }

    @GetMapping("/api/users/{userId}/stores/{storeId}/license/{licenseKey}")
    public byte[] findBusinessLicenseImgFile(@PathVariable Long userId,
                                             @PathVariable Long storeId,
                                             @PathVariable @BusinessLicenseImgKey String licenseKey) {

        // 로그인된 모든 유저가 접근 가능

        return storeService.findBusinessLicenseImgFile(storeId, userId, licenseKey);
    }

    @PostMapping("/api/users/{userId}/stores/{storeId}")
    public ResponseEntity<Void> updateStore(@PathVariable Long userId,
                                            @PathVariable Long storeId,
                                            @ModelAttribute @Valid HttpRequestCreatingStoreDto requestBody,
                                            @RequestPart(name = "businessLicenseImg") MultipartFile businessLicenseImgFile) throws IOException {

        // 해당 매장 소유자만 접근 가능

        if(businessLicenseImgFile.isEmpty()) {
            throw new EmptyImgFileException();
        }
        String fileFormat = businessLicenseImgFile.getOriginalFilename().split("[.]")[1];
        if(!ALLOWED_IMG_FILE_FORMATS.contains(fileFormat)) {
            throw new NotAllowedImgFileFormatException();
        }

        storeService.update(userId,
                storeId,
                requestBody.getStoreName(),
                requestBody.getStoreAddress(),
                requestBody.getBusinessLicenseCode(),
                businessLicenseImgFile.getInputStream());

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/api/users/{userId}/stores/{storeId}")
    public ResponseEntity<Void> changeStoreState(@PathVariable Long userId,
                                                 @PathVariable Long storeId,
                                                 @RequestBody HttpRequestChangingStoreStateDto requestBody) {

        // 관리자 계정만 접근 가능

        storeService.changeStoreState(storeId, requestBody.getState(), requestBody.getMessage());

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/api/users/{userId}/stores/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long userId,
                                            @PathVariable Long storeId) {

        // 해당 매장 소유자만 접근 가능

        storeService.delete(userId, storeId);

        return ResponseEntity.status(NO_CONTENT).build();
    }
}