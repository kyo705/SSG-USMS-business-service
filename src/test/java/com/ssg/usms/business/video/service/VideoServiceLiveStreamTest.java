package com.ssg.usms.business.video.service;

import com.ssg.usms.business.cctv.repository.Cctv;
import com.ssg.usms.business.cctv.repository.CctvRepository;
import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.exception.UnavailableStoreException;
import com.ssg.usms.business.store.repository.Store;
import com.ssg.usms.business.store.repository.StoreRepository;
import com.ssg.usms.business.video.exception.ExpiredStreamKeyException;
import com.ssg.usms.business.video.exception.NotExistingStreamKeyException;
import com.ssg.usms.business.video.exception.NotOwnedStreamKeyException;
import com.ssg.usms.business.video.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VideoServiceLiveStreamTest {

    private final String mediaServerUrl = "https://test.com";
    private VideoService videoService;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private CctvRepository cctvRepository;
    @Mock
    private VideoRepository videoRepository;

    @BeforeEach
    public void setup() {
        videoService = new VideoService(storeRepository, cctvRepository, videoRepository);
        videoService.setMediaServerUrl(mediaServerUrl);
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 유저가 자기 자신의 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 실제 파일 경로를 리턴한다.")
    @Test
    public void testGetLiveVideoWithValidParam() {

        //given
        Long userId = 1L;
        Long storeId = 10L;
        Long cctvId = 100L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";
        String filename = "test.m3u8";


        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setName("cctv1");
        cctv.setStoreId(storeId);
        cctv.setStreamKey(streamKey);
        cctv.setExpired(false);

        Store store = new Store();
        store.setId(storeId);
        store.setStoreName("무인 매장 1");
        store.setStoreAddress("서울특별시 강남구 ");
        store.setStoreState(StoreState.APPROVAL);
        store.setUserId(userId);

        given(cctvRepository.findByStreamKey(streamKey)).willReturn(cctv);
        given(storeRepository.findById(storeId)).willReturn(store);

        //when
        String redirectUrl = videoService.getLiveVideo(userId, streamKey, protocol, filename);

        //then
        assertThat(redirectUrl).startsWith(mediaServerUrl);
        verify(cctvRepository, times(1)).findByStreamKey(streamKey);
        verify(storeRepository, times(1)).findById(cctv.getStoreId());
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 존재하지 않는 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithNotExistingStreamKey() {

        //given
        Long userId = 1L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";
        String filename = "test.m3u8";

        given(cctvRepository.findByStreamKey(streamKey)).willThrow(NotExistingStreamKeyException.class);

        //when & then
        assertThrows(NotExistingStreamKeyException.class,
                () -> videoService.getLiveVideo(userId, streamKey, protocol, filename));

        verify(cctvRepository, times(1)).findByStreamKey(streamKey);
        verify(storeRepository, times(0)).findById(anyLong());
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 만료된 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithExpiredStreamKey() {

        //given
        Long userId = 1L;
        Long storeId = 10L;
        Long cctvId = 100L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";
        String filename = "test.m3u8";

        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setName("cctv1");
        cctv.setStoreId(storeId);
        cctv.setStreamKey(streamKey);
        cctv.setExpired(true);

        given(cctvRepository.findByStreamKey(streamKey)).willReturn(cctv);

        //when & then
        assertThrows(ExpiredStreamKeyException.class,
                () -> videoService.getLiveVideo(userId, streamKey, protocol, filename));

        verify(cctvRepository, times(1)).findByStreamKey(streamKey);
        verify(storeRepository, times(0)).findById(storeId);
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 타인의 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetLiveVideoWithNotOwnedStreamKey() {

        //given
        Long userId = 1L;
        Long storeId = 10L;
        Long cctvId = 100L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";
        String filename = "test.m3u8";

        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setName("cctv1");
        cctv.setStoreId(storeId);
        cctv.setStreamKey(streamKey);
        cctv.setExpired(false);

        Store store = new Store();
        store.setId(storeId);
        store.setStoreName("무인 매장 1");
        store.setStoreAddress("서울특별시 강남구 ");
        store.setStoreState(StoreState.APPROVAL);
        store.setUserId(userId + 4444);

        given(cctvRepository.findByStreamKey(streamKey)).willReturn(cctv);
        given(storeRepository.findById(cctv.getStoreId())).willReturn(store);

        //when & then
        assertThrows(NotOwnedStreamKeyException.class,
                () -> videoService.getLiveVideo(userId, streamKey, protocol, filename));

        verify(cctvRepository, times(1)).findByStreamKey(streamKey);
        verify(storeRepository, times(1)).findById(storeId);
    }

    @DisplayName("라이브 스트리밍 비디오 요청 : 요청 스트림키에 매칭된 매장이 이용 불가능 상태일 경우 예외가 발생한다.")
    @Test
    public void testGetLiveVideoWithUnavailableStore() {

        //given
        Long userId = 1L;
        Long storeId = 10L;
        Long cctvId = 100L;
        String streamKey = UUID.randomUUID().toString();
        String protocol = "hls";
        String filename = "test.m3u8";


        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setName("cctv1");
        cctv.setStoreId(storeId);
        cctv.setStreamKey(streamKey);
        cctv.setExpired(false);

        Store store = new Store();
        store.setId(storeId);
        store.setStoreName("무인 매장 1");
        store.setStoreAddress("서울특별시 강남구 ");
        store.setStoreState(StoreState.DISAPPROVAL);
        store.setUserId(userId);

        given(cctvRepository.findByStreamKey(streamKey)).willReturn(cctv);
        given(storeRepository.findById(storeId)).willReturn(store);

        //when & then
        assertThrows(UnavailableStoreException.class,
                () -> videoService.getLiveVideo(userId, streamKey, protocol, filename));

        verify(cctvRepository, times(1)).findByStreamKey(streamKey);
        verify(storeRepository, times(1)).findById(cctv.getStoreId());
    }


}