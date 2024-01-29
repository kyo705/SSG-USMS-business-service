package com.ssg.usms.business.Identification.contorller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.usms.business.Identification.dto.CertificationDto;
import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
import com.ssg.usms.business.Identification.error.NotIdentificationException;
import com.ssg.usms.business.Identification.error.NotMatchedValueAndCodeException;
import com.ssg.usms.business.Identification.service.IdentificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.IOException;

import static com.ssg.usms.business.Identification.constant.IdentificationConstant.*;
import static com.ssg.usms.business.Identification.dto.CertificationCode.EMAIL;
import static com.ssg.usms.business.Identification.dto.CertificationCode.SMS;
import static com.ssg.usms.business.user.constant.UserConstants.EMAIL_PATTERN;
import static com.ssg.usms.business.user.constant.UserConstants.PHONENUMBER_PATTERN;

@Slf4j
@RequiredArgsConstructor
@RestController
public class IdentificationController {

    private final IdentificationService identificationService;

    @PostMapping("/api/identification")
    public ResponseEntity<Void> createIdentification(@Valid @RequestBody HttpRequestIdentificationDto httpRequestIdentificationDto) throws IOException {

        if(httpRequestIdentificationDto.getCode() == EMAIL.getCode()){

            if(!httpRequestIdentificationDto.getValue().matches(EMAIL_PATTERN)){

                throw new NotMatchedValueAndCodeException(NOT_MATCHED_CODE_AND_VALUE_LITERAL);
            }
        }
        if(httpRequestIdentificationDto.getCode() == SMS.getCode()){

            if(!httpRequestIdentificationDto.getValue().matches(PHONENUMBER_PATTERN)){

                throw new NotMatchedValueAndCodeException(NOT_MATCHED_CODE_AND_VALUE_LITERAL);
            }

        }

        String Key = identificationService.createIdentification(httpRequestIdentificationDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(IDENTIFICATION_HEADER, Key );

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .build();
    }

    @GetMapping("/api/identification")
    public ResponseEntity<Void> identificationVerify(HttpServletRequest request, @RequestParam String identificationCode ) throws JsonProcessingException {

        String key = request.getHeader(IDENTIFICATION_HEADER);

        if(key == null){
            throw new NotIdentificationException(INVALID_AUTHENTICATION_CODE_LITERAL);
        }

        CertificationDto dto = CertificationDto.builder()
                .key(key)
                .value(identificationCode)
                .build();

        String jwtToken = identificationService.verifyIdentification(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, jwtToken);

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .build();
    }

}
