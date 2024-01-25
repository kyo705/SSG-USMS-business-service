package com.ssg.usms.business.video.service;

import com.ssg.usms.business.cctv.dto.CctvDto;
import com.ssg.usms.business.cctv.service.CctvService;
import com.ssg.usms.business.video.dto.HttpRequestCheckingStreamDto;
import com.ssg.usms.business.video.exception.AlreadyConnectedStreamKeyException;
import com.ssg.usms.business.video.exception.ExpiredStreamKeyException;
import com.ssg.usms.business.video.exception.NotExistingStreamKeyException;
import com.ssg.usms.business.video.repository.StreamKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StreamKeyServiceCheckingAuthOfPushingStreamTest {

    private StreamKeyService streamKeyService;
    @Mock
    private CctvService cctvService;
    @Mock
    private StreamKeyRepository streamKeyRepository;

    @BeforeEach
    public void setup() {
        streamKeyService = new StreamKeyService(cctvService, streamKeyRepository);
    }

    @DisplayName("초기 연결시 아직 연결되지 않은 자신의 스트림 키로 스트림 연결 요청시 정상적으로 검증이 끝난다.")
    @Test
    public void testCheckingAuthOfPushingStreamWithInitialConnectionWithValidStreamKey() {

        //given
        HttpRequestCheckingStreamDto requestParam = HttpRequestCheckingStreamDto
                .builder()
                .app("live")
                .name("streamKey")
                .addr("localhost:80")
                .build();

        CctvDto cctv = new CctvDto();
        cctv.setId(1L);
        cctv.setCctvName("cctv1");
        cctv.setStoreId(1L);
        cctv.setCctvStreamKey(requestParam.getName());
        cctv.setExpired(false);

        given(cctvService.findByStreamKey(requestParam.getName())).willReturn(cctv);
        given(streamKeyRepository.isExistingStreamKey(requestParam.getName())).willReturn(false);
        given(streamKeyRepository.saveStreamKey(requestParam.getName())).willReturn(requestParam.getName());

        //when & then
        streamKeyService.checkAuthOfPushingStream(requestParam);

        verify(cctvService, times(1)).findByStreamKey(requestParam.getName());
        verify(streamKeyRepository, times(1)).isExistingStreamKey(requestParam.getName());
        verify(streamKeyRepository, times(1)).saveStreamKey(requestParam.getName());
    }

    @DisplayName("자신의 기존 연결된 스트림 키에 대해 검증 요청시 정상적으로 검증이 끝난다.")
    @Test
    public void testCheckingAuthOfPushingStreamWithUpdatingConnectionWithValidStreamKey() {

        //given
        HttpRequestCheckingStreamDto requestParam = HttpRequestCheckingStreamDto
                .builder()
                .app("live")
                .name("streamKey")
                .addr("localhost:80")
                .time(System.currentTimeMillis()/1000)
                .build();

        CctvDto cctv = new CctvDto();
        cctv.setId(1L);
        cctv.setCctvName("cctv1");
        cctv.setStoreId(1L);
        cctv.setCctvStreamKey(requestParam.getName());
        cctv.setExpired(false);

        given(cctvService.findByStreamKey(requestParam.getName())).willReturn(cctv);

        //when & then
        streamKeyService.checkAuthOfPushingStream(requestParam);

        verify(cctvService, times(1)).findByStreamKey(requestParam.getName());
        verify(streamKeyRepository, times(0)).isExistingStreamKey(requestParam.getName());
        verify(streamKeyRepository, times(0)).saveStreamKey(requestParam.getName());
    }

    @DisplayName("존재하지 않는 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testCheckingAuthOfPushingStreamWithNotExistingStreamKey() {

        //given
        HttpRequestCheckingStreamDto requestParam = HttpRequestCheckingStreamDto
                .builder()
                .app("live")
                .name("streamKey")
                .addr("localhost:80")
                .build();

        given(cctvService.findByStreamKey(requestParam.getName())).willReturn(null);

        //when & then
        assertThrows(NotExistingStreamKeyException.class,
                () -> streamKeyService.checkAuthOfPushingStream(requestParam));

        verify(cctvService, times(1)).findByStreamKey(requestParam.getName());
        verify(streamKeyRepository, times(0)).isExistingStreamKey(requestParam.getName());
        verify(streamKeyRepository, times(0)).saveStreamKey(requestParam.getName());
    }

    @DisplayName("만료된 스트림 키에 매핑된 CCTV 영상 파일을 요청한 경우 예외를 발생시킨다.")
    @Test
    public void testCheckingAuthOfPushingStreamWithExpiredStreamKey() {

        //given
        HttpRequestCheckingStreamDto requestParam = HttpRequestCheckingStreamDto
                .builder()
                .app("live")
                .name("streamKey")
                .addr("localhost:80")
                .build();

        CctvDto cctv = new CctvDto();
        cctv.setId(1L);
        cctv.setCctvName("cctv1");
        cctv.setStoreId(1L);
        cctv.setCctvStreamKey(requestParam.getName());
        cctv.setExpired(true);

        given(cctvService.findByStreamKey(requestParam.getName())).willReturn(cctv);

        //when & then
        assertThrows(ExpiredStreamKeyException.class,
                () -> streamKeyService.checkAuthOfPushingStream(requestParam));

        verify(cctvService, times(1)).findByStreamKey(requestParam.getName());
        verify(streamKeyRepository, times(0)).isExistingStreamKey(requestParam.getName());
        verify(streamKeyRepository, times(0)).saveStreamKey(requestParam.getName());
    }

    @DisplayName("초기 스트림 연결시 이미 연결된 스트림 키로 스트림 연결 요청시 예외를 발생시킨다.")
    @Test
    public void testCheckingAuthOfPushingStreamWithAlreadyConnectedStream() {

        //given
        HttpRequestCheckingStreamDto requestParam = HttpRequestCheckingStreamDto
                .builder()
                .app("live")
                .name("streamKey")
                .addr("localhost:80")
                .build();

        CctvDto cctv = new CctvDto();
        cctv.setId(1L);
        cctv.setCctvName("cctv1");
        cctv.setStoreId(1L);
        cctv.setCctvStreamKey(requestParam.getName());
        cctv.setExpired(false);

        given(cctvService.findByStreamKey(requestParam.getName())).willReturn(cctv);
        given(streamKeyRepository.isExistingStreamKey(requestParam.getName())).willReturn(true);

        //when & then
        assertThrows(AlreadyConnectedStreamKeyException.class,
                () -> streamKeyService.checkAuthOfPushingStream(requestParam));

        verify(cctvService, times(1)).findByStreamKey(requestParam.getName());
        verify(streamKeyRepository, times(1)).isExistingStreamKey(requestParam.getName());
        verify(streamKeyRepository, times(0)).saveStreamKey(requestParam.getName());
    }
}
