package com.ssg.usms.business.accident.controller;

import com.ssg.usms.business.accident.dto.*;
import com.ssg.usms.business.accident.exception.InvalidDateFlowException;
import com.ssg.usms.business.accident.service.AccidentService;
import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccidentController {

    private final StoreService storeService;
    private final AccidentService accidentService;

    @PostMapping("/live-streaming/accidents")
    public ResponseEntity<Void> createAccident(@RequestBody @Valid HttpRequestCreatingAccidentDto requestBody) {

        accidentService.createAccident(requestBody.getStreamKey(),
                requestBody.getBehavior(),
                requestBody.getStartTimestamp()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/users/{userId}/stores/{storeId}/cctvs/accidents")
    public List<AccidentDto> findAllByStoreId(@PathVariable Long userId,
                                              @PathVariable Long storeId,
                                              @ModelAttribute @Valid HttpRequestRetrievingAccidentDto requestParam) {

        validateDateFlow(requestParam.getStartDate(), requestParam.getEndDate());

        userId = ((UsmsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        storeService.validateOwnedStore(storeId, userId);

        return accidentService.findByStoreId(storeId, requestParam);
    }

    @GetMapping("/api/users/{userId}/stores/{storeId}/cctvs/accidents/stats")
    public List<AccidentStatDto> findAccidentStatsByStoreId(@PathVariable Long userId,
                                                            @PathVariable Long storeId,
                                                            @ModelAttribute @Valid HttpRequestRetrievingAccidentStatDto requestParam) {

        validateDateFlow(requestParam.getStartDate(), requestParam.getEndDate());

        userId = ((UsmsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        storeService.validateOwnedStore(storeId, userId);

        return accidentService.findAccidentStatsByStoreId(storeId, requestParam.getStartDate(), requestParam.getEndDate());
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
