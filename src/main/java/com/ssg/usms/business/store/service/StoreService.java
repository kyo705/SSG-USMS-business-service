package com.ssg.usms.business.store.service;

import com.ssg.usms.business.store.dto.StoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    public List<StoreDto> getStoresByUsername(String username) {

        return null;
    }

    public void createStore(StoreDto storeDto, InputStream businessLicenseImgFile) {

    }
}
