package com.ssg.usms.business.video.service;

import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.dto.CctvDto;
import com.ssg.usms.business.store.dto.StoreDto;
import com.ssg.usms.business.store.service.CctvService;
import com.ssg.usms.business.store.service.StoreService;
import com.ssg.usms.business.video.exception.*;
import com.ssg.usms.business.video.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VideoServiceLiveStreamTest {

    private final String mediaServerUrl = "https://test.com";
    private VideoService videoService;
    @Mock
    private VideoRepository videoRepository;
    @Mock
    private StoreService storeService;
    @Mock
    private CctvService cctvService;

    @BeforeEach
    public void setup() {
        videoService = new VideoService(storeService, cctvService, videoRepository);
        videoService.setMediaServerUrl(mediaServerUrl);
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 유저가 자기 자신의 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 실제 파일 경로를 리턴한다.")
    @Test
    public void testGetLiveVideoWithValidParam() {

        //given
        String username = "kyo705";
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "hls";
        String filename = "test.m3u8";

        CctvDto cctv = new CctvDto();
        cctv.setId(1L);
        cctv.setCctvName("cctv1");
        cctv.setStoreId(1L);
        cctv.setCctvStreamKey(streamKey);
        cctv.setExpired(false);

        given(cctvService.getCctvByStreamKey(streamKey)).willReturn(cctv);

        List<StoreDto> stores = new ArrayList<>();
        StoreDto store1 = new StoreDto();
        store1.setId(1L);
        store1.setName("무인 매장 1");
        store1.setStoreState(StoreState.APPROVAL);
        store1.setUserId(1L);
        stores.add(store1);

        given(storeService.getStoresByUsername(username)).willReturn(stores);

        //when
        String redirectUrl = videoService.getLiveVideo(username, streamKey, protocol, filename);

        //then
        assertThat(redirectUrl).startsWith(mediaServerUrl);
        verify(cctvService, times(1)).getCctvByStreamKey(streamKey);
        verify(storeService, times(1)).getStoresByUsername(username);
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 허용되지 않은 스트림 프토로콜 타입으로 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithNotAllowedStreamingProtocol() {

        //given
        String username = "kyo705";
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "ftp";
        String filename = "test.m3u8";

        //when & then
        assertThrows(NotAllowedStreamingProtocolException.class,
                () -> videoService.getLiveVideo(username, streamKey, protocol, filename));

        verify(cctvService, times(0)).getCctvByStreamKey(streamKey);
        verify(storeService, times(0)).getStoresByUsername(username);
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 스트림 프토로콜 타입과 불일치하는 파일 확장자로 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithNotMatchingStreamProtocolAndFileFormat() {

        //given
        String username = "kyo705";
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "hls";
        String filename = "test.mp4";

        //when & then
        assertThrows(NotMatchingStreamingProtocolAndFileFormatException.class,
                () -> videoService.getLiveVideo(username, streamKey, protocol, filename));

        verify(cctvService, times(0)).getCctvByStreamKey(streamKey);
        verify(storeService, times(0)).getStoresByUsername(username);
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 존재하지 않는 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithExpiredStreamKey() {

        //given
        String username = "kyo705";
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "hls";
        String filename = "test.m3u8";

        given(cctvService.getCctvByStreamKey(streamKey)).willReturn(null);

        //when & then
        assertThrows(NotExistingStreamKeyException.class,
                () -> videoService.getLiveVideo(username, streamKey, protocol, filename));

        verify(cctvService, times(1)).getCctvByStreamKey(streamKey);
        verify(storeService, times(0)).getStoresByUsername(username);
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 만료된 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithNotExistingStreamKey() {

        //given
        String username = "kyo705";
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "hls";
        String filename = "test.m3u8";

        CctvDto cctv = new CctvDto();
        cctv.setId(1L);
        cctv.setCctvName("cctv1");
        cctv.setStoreId(1L);
        cctv.setCctvStreamKey(streamKey);
        cctv.setExpired(true);

        given(cctvService.getCctvByStreamKey(streamKey)).willReturn(cctv);

        //when & then
        assertThrows(ExpiredStreamKeyException.class,
                () -> videoService.getLiveVideo(username, streamKey, protocol, filename));

        verify(cctvService, times(1)).getCctvByStreamKey(streamKey);
        verify(storeService, times(0)).getStoresByUsername(username);
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 타인의 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithNotOwnedStreamKey() {

        //given
        String username = "kyo705";
        String streamKey = UUID.randomUUID().toString().replace("-", "");
        String protocol = "hls";
        String filename = "test.m3u8";

        CctvDto cctv = new CctvDto();
        cctv.setId(1L);
        cctv.setCctvName("cctv1");
        cctv.setStoreId(1L);                       // 스트림 키에 매핑된 매장 아이디 : 1
        cctv.setCctvStreamKey(streamKey);
        cctv.setExpired(false);

        given(cctvService.getCctvByStreamKey(streamKey)).willReturn(cctv);

        List<StoreDto> stores = new ArrayList<>();
        StoreDto store1 = new StoreDto();
        store1.setId(2L);                           // 해당 유저의 보유 매장 아이디 : 2
        store1.setName("무인 매장 1");
        store1.setStoreState(StoreState.APPROVAL);
        store1.setUserId(1L);
        stores.add(store1);

        given(storeService.getStoresByUsername(username)).willReturn(stores);

        //when & then
        assertThrows(NotOwnedStreamKeyException.class,
                () -> videoService.getLiveVideo(username, streamKey, protocol, filename));

        verify(cctvService, times(1)).getCctvByStreamKey(streamKey);
        verify(storeService, times(1)).getStoresByUsername(username);
    }


}
