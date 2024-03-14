package com.ssg.usms.business.warning.controller;

import com.ssg.usms.business.accident.exception.InvalidDateFlowException;
import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.store.exception.UnavailableStoreException;
import com.ssg.usms.business.store.service.StoreService;
import com.ssg.usms.business.warning.dto.HttpRequestRetrievingRegionWarningDto;
import com.ssg.usms.business.warning.dto.RegionWarningDto;
import com.ssg.usms.business.warning.service.RegionWarningService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RegionWarningController {

    private final StoreService storeService;
    private final RegionWarningService regionWarningService;

    @GetMapping("/api/users/{userId}/stores/{storeId}/accidents/region")
    public List<RegionWarningDto> findAllByStoreId(@PathVariable Long userId,
                                                   @PathVariable Long storeId,
                                                   @ModelAttribute @Valid HttpRequestRetrievingRegionWarningDto requestParams) {

        validateDateFlow(requestParams.getStartDate(), requestParams.getEndDate());

        userId = ((UsmsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        storeService.validateOwnedStore(storeId, userId);
        if(!storeService.isAvailable(storeId)) {
            throw new UnavailableStoreException();
        }

        return regionWarningService.findByRegion(storeId,
                                                requestParams.getStartDate(),
                                                requestParams.getEndDate(),
                                                requestParams.getRegionWarningId(),
                                                requestParams.getSize());
    }

    private void validateDateFlow(String startDate, String endDate) {

        if(startDate == null || endDate == null) {
            return;
        }
        if(LocalDate.parse(startDate).isAfter(LocalDate.parse(endDate))) {
            throw new InvalidDateFlowException();
        }
    }
}
