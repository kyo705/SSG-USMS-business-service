package com.ssg.usms.business.video.controller;

import com.ssg.usms.business.video.annotation.LiveVideoFilename;
import com.ssg.usms.business.video.annotation.StreamKey;
import com.ssg.usms.business.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @GetMapping("/video/{streamKey}/live/{protocol}/{filename}")
    public ResponseEntity<Void> getLiveVideo(@PathVariable String protocol,
                                             @PathVariable @StreamKey String streamKey,
                                             @PathVariable @LiveVideoFilename String filename) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String redirectUrl = videoService.getLiveVideo(username, streamKey, protocol, filename);

        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }
}
