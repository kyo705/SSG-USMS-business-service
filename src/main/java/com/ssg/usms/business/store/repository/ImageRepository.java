package com.ssg.usms.business.store.repository;

import com.ssg.usms.business.store.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageRepository {

    void save(String key, MultipartFile businessLicenseImgFile);

    ImageDto find(String key);

    boolean isExisting(String key);
}
