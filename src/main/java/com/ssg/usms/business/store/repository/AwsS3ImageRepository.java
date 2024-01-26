package com.ssg.usms.business.store.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;

@Repository
@RequiredArgsConstructor
public class AwsS3ImageRepository implements ImageRepository {

    private final AmazonS3 amazonS3;
    @Value("${aws.s3.image-bucket}")
    private String imageBucket;

    @Override
    public void save(String key, InputStream inputStream) {

        ObjectMetadata metadata = new ObjectMetadata();
        try {
            metadata.setContentLength(inputStream.read());
            amazonS3.putObject(imageBucket, key, inputStream, metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
