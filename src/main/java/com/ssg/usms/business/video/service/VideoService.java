package com.ssg.usms.business.video.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.ssg.usms.business.store.CctvDto;
import com.ssg.usms.business.store.CctvService;
import com.ssg.usms.business.store.StoreDto;
import com.ssg.usms.business.store.StoreService;
import com.ssg.usms.business.video.exception.ExpiredStreamKeyException;
import com.ssg.usms.business.video.exception.NotExistingStreamKeyException;
import com.ssg.usms.business.video.exception.NotOwnedStreamKeyException;
import com.ssg.usms.business.video.util.ProtocolAndFileFormatMatcher;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Setter
@Service
@RequiredArgsConstructor
public class VideoService {

    private final AmazonS3 amazonS3;
    private final StoreService storeService;
    private final CctvService cctvService;

    @Value("${usms.media-server.url}")
    private String mediaServerUrl;
    @Value("${aws.s3.transcode-video-bucket}")
    private String transcodeVideoBucket;

    @Transactional(readOnly = true)
    public String getLiveVideo(String username, String streamKey, String protocol, String filename) {

        validate(username, streamKey, protocol, filename);

        // 해당 파일에 대한 URL 리다이렉트
        return String.format("%s/video/%s/live/%s/%s", mediaServerUrl, protocol, streamKey, filename);
    }

    @Transactional(readOnly = true)
    public byte[] getReplayVideo(String username, String streamKey, String protocol, String filename) {

        validate(username, streamKey, protocol, filename);

        // filename : streamKey-1641900000000.m3u8 or streamKey-1641900000000-001.ts
        long timestamp = Long.parseLong(filename.split("[.]")[0].split("-")[1]);
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());

        // 실제 다시보기 파일 경로 : /streamKey/년/월/일/파일명
        String replayVideoRealPath = Paths.get(
                                            streamKey,
                                            Integer.toString(dateTime.getYear()),
                                            Integer.toString(dateTime.getMonth().getValue()),
                                            Integer.toString(dateTime.getDayOfMonth()),
                                            filename
                                    ).toString();

        S3Object s3Object = amazonS3.getObject(transcodeVideoBucket, replayVideoRealPath);

        try {
            return s3Object.getObjectContent().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validate(String username, String streamKey, String protocol, String filename) {

        String fileFormat = filename.split("[.]")[1];
        ProtocolAndFileFormatMatcher.matches(protocol, fileFormat);

        CctvDto cctvDto = cctvService.getCctvByStreamKey(streamKey);
        if(cctvDto == null) {
            throw new NotExistingStreamKeyException();
        }
        if(cctvDto.isExpired()) {
            throw new ExpiredStreamKeyException();
        }

        List<StoreDto> stores = storeService.getStoresByUsername(username);
        if(stores == null || stores.isEmpty()) {
            throw new NotOwnedStreamKeyException();
        }

        boolean isOwned = false;
        for(StoreDto store : stores) {
            if(store.getId() != cctvDto.getStoreId()) {
                continue;
            }
            isOwned = true;
            break;
        }
        if(!isOwned) {
            throw new NotOwnedStreamKeyException();
        }
    }
}
