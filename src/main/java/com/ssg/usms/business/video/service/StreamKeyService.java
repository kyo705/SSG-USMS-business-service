package com.ssg.usms.business.video.service;


import com.ssg.usms.business.cctv.dto.CctvDto;
import com.ssg.usms.business.cctv.service.CctvService;
import com.ssg.usms.business.video.dto.HttpRequestCheckingStreamDto;
import com.ssg.usms.business.video.exception.AlreadyConnectedStreamKeyException;
import com.ssg.usms.business.video.exception.ExpiredStreamKeyException;
import com.ssg.usms.business.video.exception.NotExistingStreamKeyException;
import com.ssg.usms.business.video.repository.StreamKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StreamKeyService {

    private final CctvService cctvService;
    private final StreamKeyRepository streamKeyRepository;

    @Transactional
    public void checkAuthOfPushingStream(HttpRequestCheckingStreamDto requestParam) {

        String streamKey = requestParam.getName();

        // 해당 키 값이 유효한지 체크
        CctvDto cctvDto = cctvService.getCctvByStreamKey(streamKey);
        if(cctvDto == null) {
            throw new NotExistingStreamKeyException();
        }
        if(cctvDto.isExpired()) {
            throw new ExpiredStreamKeyException();
        }

        long connectedTime = requestParam.getTime();
        // 이미 기존에 연결된 상태일 경우 리턴
        if(connectedTime != 0L) {
            return;
        }
        // 초기 연결 시 해당 키 값이 이미 연결된 상태인지 체크
        if(streamKeyRepository.isExistingStreamKey(streamKey)) {
            throw new AlreadyConnectedStreamKeyException();
        }
    }
}
