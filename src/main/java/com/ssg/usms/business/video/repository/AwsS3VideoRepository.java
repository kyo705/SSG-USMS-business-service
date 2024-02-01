package com.ssg.usms.business.video.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import okio.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.io.IOException;
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

    @Cacheable(value = "replayVideoFileList", key =  "#path", cacheManager = "usmsCacheManager")
    @Override
    public List<String> getVideoFilenames(String path) {

        return amazonS3.listObjects(transcodeVideoBucket, path)
                .getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .filter(key-> key.endsWith(".m3u8"))
                .map(key -> {
                    int idx = key.lastIndexOf(Path.DIRECTORY_SEPARATOR);
                    return key.substring(idx+1);
                })
                .collect(Collectors.toList());
    }
}
