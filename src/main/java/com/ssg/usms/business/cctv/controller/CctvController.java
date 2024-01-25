package com.ssg.usms.business.cctv.controller;

import com.ssg.usms.business.cctv.dto.CctvDto;
import com.ssg.usms.business.cctv.dto.HttpRequestCreatingCctvDto;
import com.ssg.usms.business.cctv.dto.HttpRequestUpdatingCctvDto;
import com.ssg.usms.business.cctv.service.CctvService;
import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.store.exception.UnavailableStoreException;
import com.ssg.usms.business.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE;
import static com.ssg.usms.business.constant.CustomStatusCode.NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE;
import static com.ssg.usms.business.user.dto.UserRole.ROLE_ADMIN;

@Validated
@RestController
@RequiredArgsConstructor
public class CctvController {

    private final StoreService storeService;
    private final CctvService cctvService;

    @PostMapping("api/users/{userId}/stores/{storeId}/cctvs")
    public ResponseEntity<Void> createCctv(@PathVariable Long userId,
                                           @PathVariable Long storeId,
                                           @RequestBody @Valid HttpRequestCreatingCctvDto requestBody) {

        // 검증
        userId = ((UsmsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        storeService.validateOwnedStore(storeId, userId);
        if(!storeService.isAvailable(userId)) {
            throw new UnavailableStoreException();
        }

        // 비지니스 로직
        cctvService.createCctv(storeId, requestBody.getName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("api/users/{userId}/stores/{storeId}/cctvs/{cctvId}")
    public CctvDto findById(@PathVariable Long userId,
                            @PathVariable Long storeId,
                            @PathVariable Long cctvId) {

        List<? extends GrantedAuthority> authorities = (List<? extends GrantedAuthority>) SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities();

        if (authorities.size() != 1) {
            throw new IllegalStateException("유저 권한 갯수가 현재 이상함");
        }
        //관리자가 아닐 경우 자신이 소유한 cctv인지 확인
        if (!authorities.get(0).getAuthority().equals(ROLE_ADMIN.name())) {
            userId = ((UsmsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            storeService.validateOwnedStore(storeId, userId);
            cctvService.validateOwnedCctv(storeId, cctvId);
            if(!storeService.isAvailable(userId)) {
                throw new UnavailableStoreException();
            }
        }

        // 비지니스 로직
        return cctvService.findById(cctvId);
    }

    @GetMapping("api/users/{userId}/stores/{storeId}/cctvs")
    public List<CctvDto> findAllByStoreId(@PathVariable Long userId,
                                          @PathVariable Long storeId,
                                          @RequestParam(required = false)
                                              @NotNull(message = NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE)
                                              @PositiveOrZero(message = NOT_ALLOWED_PAGE_OFFSET_FORMAT_MESSAGE)
                                              Integer offset,
                                          @RequestParam(required = false)
                                              @NotNull(message = NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE)
                                              @Positive(message = NOT_ALLOWED_PAGE_SIZE_FORMAT_MESSAGE)
                                              Integer size) {

        List<? extends GrantedAuthority> authorities = (List<? extends GrantedAuthority>) SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities();

        if (authorities.size() != 1) {
            throw new IllegalStateException("유저 권한 갯수가 현재 이상함");
        }
        //관리자가 아닐 경우 자신이 소유한 매장인지 확인
        if (!authorities.get(0).getAuthority().equals(ROLE_ADMIN.name())) {
            userId = ((UsmsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            storeService.validateOwnedStore(storeId, userId);
            if(!storeService.isAvailable(userId)) {
                throw new UnavailableStoreException();
            }
        }

        // 비지니스 로직
        return cctvService.findAllByStoreId(storeId, offset, size);
    }

    @PatchMapping("api/users/{userId}/stores/{storeId}/cctvs/{cctvId}")
    public ResponseEntity<Void> update(@PathVariable Long userId,
                                       @PathVariable Long storeId,
                                       @PathVariable Long cctvId,
                                       @RequestBody @Valid HttpRequestUpdatingCctvDto requestBody) {

        // 본인 소유의 cctv가 맞는지 검증
        userId = ((UsmsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        storeService.validateOwnedStore(storeId, userId);
        cctvService.validateOwnedCctv(storeId, cctvId);
        if(!storeService.isAvailable(userId)) {
            throw new UnavailableStoreException();
        }

        // 비지니스 로직
        cctvService.changeCctvName(cctvId, requestBody.getName());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("api/users/{userId}/stores/{storeId}/cctvs/{cctvId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId,
                                       @PathVariable Long storeId,
                                       @PathVariable Long cctvId) {

        // 본인 소유의 cctv가 맞는지 검증
        userId = ((UsmsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        storeService.validateOwnedStore(storeId, userId);
        cctvService.validateOwnedCctv(storeId, cctvId);
        if(!storeService.isAvailable(userId)) {
            throw new UnavailableStoreException();
        }

        // 비지니스 로직
        cctvService.deleteCctv(cctvId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
