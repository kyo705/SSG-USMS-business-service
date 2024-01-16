package com.ssg.usms.business.video.controller;

import com.ssg.usms.business.video.dto.HttpRequestCheckingStreamDto;
import com.ssg.usms.business.video.service.StreamKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StreamKeyController {

    private final StreamKeyService streamKeyService;

    @PostMapping("/live-streaming/checking")
    public ResponseEntity<Void> checkAuthOfPushingStream(@ModelAttribute HttpRequestCheckingStreamDto requestParam) {

        streamKeyService.checkAuthOfPushingStream(requestParam);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
