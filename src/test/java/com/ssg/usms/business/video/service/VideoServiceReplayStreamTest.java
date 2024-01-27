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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VideoServiceReplayStreamTest {

    private VideoService videoService;
    @Mock
    private VideoRepository videoRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private CctvRepository cctvRepository;

    @BeforeEach
    public void setup() {
        videoService = new VideoService(storeRepository, cctvRepository, videoRepository);
    }

    @DisplayName("정상적인 파라미터로 요청시 영상 데이터를 리턴한다.")
    @Test
    public void testGetReplayVideoWithValidParam() throws IOException {

        //given
        Long userId = 1L;
        Long storeId = 10L;
        Long cctvId = 100L;
        String streamKey = UUID.randomUUID().toString();
        String filename = streamKey + "-" + (System.currentTimeMillis()/1000) + ".m3u8";

        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setName("cctv1");
        cctv.setStoreId(storeId);
        cctv.setStreamKey(streamKey);
        cctv.setExpired(false);

        Store store = new Store();
        store.setId(storeId);
        store.setStoreName("무인 매장 1");
        store.setStoreAddress("부산광역시 해운대구 ");
        store.setStoreState(StoreState.APPROVAL);
        store.setUserId(userId);

        given(cctvRepository.findByStreamKey(streamKey)).willReturn(cctv);
        given(storeRepository.findById(storeId)).willReturn(store);

        String filePath = "test2-1704442782.m3u8";
        ClassPathResource resource = new ClassPathResource(filePath);
        given(videoRepository.getVideo(anyString())).willReturn(resource.getInputStream().readAllBytes());

        //when
        byte[] fileData = videoService.getReplayVideo(userId, streamKey, filename);

        //then
        Assertions.assertThat(fileData).isEqualTo(resource.getInputStream().readAllBytes());

        verify(cctvRepository, times(1)).findByStreamKey(streamKey);
        verify(storeRepository, times(1)).findById(cctv.getStoreId());
        verify(videoRepository, times(1)).getVideo(anyString());
    }

    @DisplayName("만료된 라이브 스트림 키로 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetReplayVideoWithExpiredStreamKey() {

        //given
        Long userId = 1L;
        Long storeId = 10L;
        Long cctvId = 100L;
        String streamKey = UUID.randomUUID().toString();
        String filename = streamKey + "-" + (System.currentTimeMillis()/1000) + ".m3u8";

        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setName("cctv1");
        cctv.setStoreId(storeId);
        cctv.setStreamKey(streamKey);
        cctv.setExpired(true);

        given(cctvRepository.findByStreamKey(streamKey)).willReturn(cctv);

        //when & then
        assertThrows(ExpiredStreamKeyException.class,
                () -> videoService.getReplayVideo(userId, streamKey, filename));

        verify(cctvRepository, times(1)).findByStreamKey(streamKey);
        verify(storeRepository, times(0)).findById(cctv.getStoreId());
        verify(videoRepository, times(0)).getVideo(anyString());
    }

    @DisplayName("만료된 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetReplayVideoWithNotExistingStreamKey() {

        //given
        Long userId = 1L;
        Long storeId = 10L;
        Long cctvId = 100L;
        String streamKey = UUID.randomUUID().toString();
        String filename = streamKey + "-" + (System.currentTimeMillis()/1000) + ".m3u8";

        given(cctvRepository.findByStreamKey(streamKey)).willThrow(NotExistingStreamKeyException.class);

        //when & then
        assertThrows(NotExistingStreamKeyException.class,
                () -> videoService.getReplayVideo(userId, streamKey, filename));

        verify(cctvRepository, times(1)).findByStreamKey(streamKey);
        verify(storeRepository, times(0)).findById(anyLong());
        verify(videoRepository, times(0)).getVideo(anyString());
    }

    @DisplayName("타인의 라이브 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testGetReplayVideoWithNotOwnedStreamKey() {

        //given
        Long userId = 1L;
        Long storeId = 10L;
        Long cctvId = 100L;
        String streamKey = UUID.randomUUID().toString();
        String filename = streamKey + "-" + (System.currentTimeMillis()/1000) + ".m3u8";

        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setName("cctv1");
        cctv.setStoreId(storeId);
        cctv.setStreamKey(streamKey);
        cctv.setExpired(false);

        Store store = new Store();
        store.setId(storeId);
        store.setStoreName("무인 매장 1");
        store.setStoreAddress("부산광역시 해운대구 ");
        store.setStoreState(StoreState.APPROVAL);
        store.setUserId(userId + 100);

        given(cctvRepository.findByStreamKey(streamKey)).willReturn(cctv);
        given(storeRepository.findById(storeId)).willReturn(store);

        //when & then
        assertThrows(NotOwnedStreamKeyException.class,
                () -> videoService.getReplayVideo(userId, streamKey, filename));

        verify(cctvRepository, times(1)).findByStreamKey(streamKey);
        verify(storeRepository, times(1)).findById(cctv.getStoreId());
        verify(videoRepository, times(0)).getVideo(anyString());
    }

    @DisplayName("요청한 스트림 키를 보유한 매장이 이용 불가능할 경우 예외를 발생시킨다.")
    @Test
    public void testGetReplayVideoWithUnavailableStore() {

        //given
        Long userId = 1L;
        Long storeId = 10L;
        Long cctvId = 100L;
        String streamKey = UUID.randomUUID().toString();
        String filename = streamKey + "-" + (System.currentTimeMillis()/1000) + ".m3u8";

        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setName("cctv1");
        cctv.setStoreId(storeId);
        cctv.setStreamKey(streamKey);
        cctv.setExpired(false);

        Store store = new Store();
        store.setId(storeId);
        store.setStoreName("무인 매장 1");
        store.setStoreAddress("부산광역시 해운대구 ");
        store.setStoreState(StoreState.STOPPED);
        store.setUserId(userId);

        given(cctvRepository.findByStreamKey(streamKey)).willReturn(cctv);
        given(storeRepository.findById(storeId)).willReturn(store);

        //when & then
        assertThrows(UnavailableStoreException.class,
                () -> videoService.getReplayVideo(userId, streamKey, filename));

        verify(cctvRepository, times(1)).findByStreamKey(streamKey);
        verify(storeRepository, times(1)).findById(cctv.getStoreId());
        verify(videoRepository, times(0)).getVideo(anyString());
    }
}
