package com.ssg.usms.business.video.service;

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

import java.util.List;

@Setter
@Service
@RequiredArgsConstructor
public class VideoService {

    private final StoreService storeService;
    private final CctvService cctvService;
    @Value("${usms.media-server.url")
    private String mediaServerUrl;

    @Transactional
    public String getLiveVideo(String username, String streamKey, String protocol, String filename) {

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
        // 해당 파일에 대한 URL 리다이렉트
        return String.format("%s/video/%s/live/%s/%s", mediaServerUrl, streamKey, protocol, filename);
    }

    public byte[] getReplayVideo(String username, String streamKey, String protocol, String filename) {

        return null;
    }
}
