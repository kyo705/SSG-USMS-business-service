package com.ssg.usms.business.store.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.ssg.usms.business.store.dto.ImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.ssg.usms.business.config.CacheConfiguration.IMG_FILE_CACHE_KEY;

@Repository
@RequiredArgsConstructor
public class AwsS3ImageRepository implements ImageRepository {

    private final AmazonS3 amazonS3;
    @Value("${aws.s3.image-bucket}")
    private String imageBucket;

    @CachePut(value = IMG_FILE_CACHE_KEY, key = "#key", cacheManager = "usmsCacheManager")
    @Override
    public void save(String key, MultipartFile businessLicenseImgFile) {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(businessLicenseImgFile.getSize());
        metadata.setContentType(businessLicenseImgFile.getContentType());

        try {
            amazonS3.putObject(imageBucket, key, businessLicenseImgFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable(value = IMG_FILE_CACHE_KEY, key =  "#key", cacheManager = "usmsCacheManager")
    @Override
    public ImageDto find(String key) {

        try {
            S3Object result = amazonS3.getObject(imageBucket, key);
            String contentType = result.getObjectMetadata().getContentType();
            long contentLength = result.getObjectMetadata().getContentLength();
            byte[] content = result.getObjectContent().readAllBytes();

            return new ImageDto(contentType, contentLength, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isExisting(String key) {

        return amazonS3.doesObjectExist(imageBucket, key);
    }
}
