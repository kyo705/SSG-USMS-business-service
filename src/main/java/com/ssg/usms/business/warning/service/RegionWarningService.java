package com.ssg.usms.business.warning.service;

import com.ssg.usms.business.store.repository.Store;
import com.ssg.usms.business.store.repository.StoreRepository;
import com.ssg.usms.business.warning.dto.RegionWarningDto;
import com.ssg.usms.business.warning.repository.RegionWarningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionWarningService {

    private final StoreRepository storeRepository;
    private final RegionWarningRepository regionWarningRepository;

    @Transactional(readOnly = true)
    public List<RegionWarningDto> findByRegion(Long storeId, String startDate, String endDate, int offset, int size) {

        Store store = storeRepository.findById(storeId);
        String[] address = store.getStoreAddress().split(" "); // 서울특별시 강남구 강남대로42길

        String region = address[0] + " " + address[1];
        if(startDate == null) {
            startDate = LocalDate.MIN.toString();
        }
        if(endDate == null) {
            endDate = LocalDate.now().toString();
        }

        return regionWarningRepository.findByRegion(region, startDate, endDate, offset, size)
                .stream()
                .map(RegionWarningDto::new)
                .collect(Collectors.toList());
    }



}
