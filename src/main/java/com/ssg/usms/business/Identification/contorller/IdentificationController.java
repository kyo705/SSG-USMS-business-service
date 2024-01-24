package com.ssg.usms.business.Identification.contorller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.usms.business.Identification.service.IdentificationService;
import com.ssg.usms.business.Identification.dto.CertificationDto;
import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
import com.ssg.usms.business.Identification.error.NotIdentificationException;
import com.ssg.usms.business.Identification.error.NotMatchedValueAndCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.ssg.usms.business.Identification.constant.IdenticationConstant.*;
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
    public ResponseEntity Createidentification(@Valid @RequestBody HttpRequestIdentificationDto httpRequestIdentificationDto) throws JsonProcessingException {

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
    public ResponseEntity identificationVerify(HttpServletRequest request, @RequestParam("identificationCode") String identificationCode ) throws JsonProcessingException {

        String Key = String.valueOf(request.getHeaders(IDENTIFICATION_HEADER));

        if(Key == null){
            throw new NotIdentificationException(INVALID_AUTHENTICATION_CODE_LITERAL);
        }

        CertificationDto dto = CertificationDto.builder()
                .key(Key)
                .value(identificationCode)
                .build();

        String jwtToken = identificationService.verifyIdentification(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION,jwtToken);

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .build();
    }

}