package com.ssg.usms.business.store.controller;

import com.ssg.usms.business.store.dto.HttpRequestCreatingStoreDto;
import com.ssg.usms.business.store.dto.StoreDto;
import com.ssg.usms.business.store.exception.EmptyImgFileException;
import com.ssg.usms.business.store.exception.NotAllowedImgFileFormatException;
import com.ssg.usms.business.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.util.List;

import static com.ssg.usms.business.store.constant.StoreConstants.ALLOWED_IMG_FILE_FORMATS;

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

        // userId와 세션 정보 매칭
        //SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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

}
