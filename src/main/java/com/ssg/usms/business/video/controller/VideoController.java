package com.ssg.usms.business.video.controller;

import com.ssg.usms.business.cctv.service.CctvService;
import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.store.exception.UnavailableStoreException;
import com.ssg.usms.business.store.service.StoreService;
import com.ssg.usms.business.video.annotation.LiveVideoFilename;
import com.ssg.usms.business.video.annotation.ReplayVideoFilename;
import com.ssg.usms.business.video.annotation.StreamKey;
import com.ssg.usms.business.video.service.VideoService;
import com.ssg.usms.business.video.util.ProtocolAndFileFormatMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.ssg.usms.business.accident.constant.AccidentConstants.DATE_FORMAT_REGEX;
import static com.ssg.usms.business.constant.CustomStatusCode.INVALID_DATE_FORMAT_MESSAGE;

@Validated
@RestController
@RequiredArgsConstructor
public class VideoController {

    private final StoreService storeService;
    private final CctvService cctvService;
    private final VideoService videoService;

    @GetMapping("/video/{protocol}/live/{streamKey}/{filename}")
    public ResponseEntity<Void> getLiveVideo(@PathVariable String protocol,
                                             @PathVariable @StreamKey String streamKey,
                                             @PathVariable @LiveVideoFilename String filename) {

        validateFileFormat(protocol, filename);

        Long userId = ((UsmsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        String redirectUrl = videoService.getLiveVideo(userId, streamKey, protocol, filename);

        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }

    @GetMapping("/video/{protocol}/replay/{streamKey}/{filename}")
    public ResponseEntity<byte[]> getReplayVideo(@PathVariable String protocol,
                                                 @PathVariable @StreamKey String streamKey,
                                                 @PathVariable @ReplayVideoFilename String filename) {
        validateFileFormat(protocol, filename);

        Long userId = ((UsmsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        byte[] videoStream = videoService.getReplayVideo(userId, streamKey, filename);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(videoStream);
    }

    @GetMapping("/api/users/{userId}/stores/{storeId}/cctvs/{cctvId}/replay")
    public List<String> getReplayVideoFilenames(@PathVariable Long userId,
                                                @PathVariable Long storeId,
                                                @PathVariable Long cctvId,
                                                @RequestParam
                                                    @NotEmpty(message = INVALID_DATE_FORMAT_MESSAGE)
                                                    @Pattern(regexp = DATE_FORMAT_REGEX, message = INVALID_DATE_FORMAT_MESSAGE)
                                                    String date) {

        userId = ((UsmsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        storeService.validateOwnedStore(storeId, userId);
        cctvService.validateOwnedCctv(storeId, cctvId);
        if(!storeService.isAvailable(storeId)) {
            throw new UnavailableStoreException();
        }

        return videoService.getReplayVideoFilenames(cctvId, date);
    }

    private void validateFileFormat(String protocol, String filename) {

        String fileFormat = filename.split("[.]")[1];
        ProtocolAndFileFormatMatcher.matches(protocol, fileFormat);
    }
}
