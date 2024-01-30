package com.ssg.usms.business.video.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.KeyFilter;
import com.amazonaws.services.s3.transfer.TransferManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AwsS3VideoRepository implements VideoRepository {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.transcode-video-bucket}")
    private String transcodeVideoBucket;

    @Override
    public byte[] getVideo(String key) {

        S3Object s3Object = amazonS3.getObject(transcodeVideoBucket, key);
        try {
            return s3Object.getObjectContent().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getVideoFilenames(String path) {

        return amazonS3.listObjects(transcodeVideoBucket, path)
                .getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .filter(key-> key.endsWith(".m3u8"))
                .collect(Collectors.toList());
    }
}
