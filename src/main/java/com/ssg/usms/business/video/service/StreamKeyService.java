package com.ssg.usms.business.video.service;


import com.ssg.usms.business.video.dto.HttpRequestCheckingStreamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StreamKeyService {

    @Transactional
    public void checkAuthOfPushingStream(HttpRequestCheckingStreamDto requestParam) {

    }
}
