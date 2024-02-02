package com.ssg.usms.business.store.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;

import static com.ssg.usms.business.config.CacheConfiguration.IMG_FILE_CACHE_KEY;

@Repository
@RequiredArgsConstructor
public class AwsS3ImageRepository implements ImageRepository {

    private final AmazonS3 amazonS3;
    @Value("${aws.s3.image-bucket}")
    private String imageBucket;

    @CachePut(value = IMG_FILE_CACHE_KEY, key = "#key", cacheManager = "usmsCacheManager")
    @Override
    public void save(String key, InputStream inputStream, long fileSize) {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);

        amazonS3.putObject(imageBucket, key, inputStream, metadata);
    }

    @Cacheable(value = IMG_FILE_CACHE_KEY, key =  "#key", cacheManager = "usmsCacheManager")
    @Override
    public byte[] find(String key) {

        try {
            return amazonS3.getObject(imageBucket, key).getObjectContent().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isExisting(String key) {

        return amazonS3.doesObjectExist(imageBucket, key);
    }
}
