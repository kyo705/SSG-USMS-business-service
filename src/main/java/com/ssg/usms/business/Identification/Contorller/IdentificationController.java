package com.ssg.usms.business.Identification.Contorller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.usms.business.Identification.dto.CertificationDto;
import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
import com.ssg.usms.business.Identification.dto.HttpResponseIdentificationDto;
import com.ssg.usms.business.Identification.Service.SmsService;
import com.ssg.usms.business.Identification.error.NotIdentificationException;
import com.ssg.usms.business.Identification.error.NotMatchedValueAndCodeException;
import com.ssg.usms.business.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.UUID;

import static com.ssg.usms.business.Identification.constant.IdenticationConstant.*;
import static com.ssg.usms.business.user.constant.UserConstants.EMAIL_PATTERN;
import static com.ssg.usms.business.user.constant.UserConstants.PHONENUMBER_PATTERN;

@Slf4j
@RequiredArgsConstructor
@RestController
public class IdentificationController {

    private final SmsService smsService;

    private final JwtUtil jwtUtil;

    @PostMapping("/api/identification")
    public ResponseEntity<HttpResponseIdentificationDto> identification(@Valid @RequestBody HttpRequestIdentificationDto httpRequestIdentificationDto, HttpServletRequest request) throws JsonProcessingException {

        if(httpRequestIdentificationDto.getCode() == 0){

            if(!httpRequestIdentificationDto.getValue().matches(EMAIL_PATTERN)){

                throw new NotMatchedValueAndCodeException(NOT_MATCHED_CODE_AND_VALUE_LITERAL);
            }
        }
        if(httpRequestIdentificationDto.getCode() == 1){

            if(!httpRequestIdentificationDto.getValue().matches(PHONENUMBER_PATTERN)){

                throw new NotMatchedValueAndCodeException(NOT_MATCHED_CODE_AND_VALUE_LITERAL);
            }
        }

        String Key = UUID.randomUUID().toString();

        smsService.sendSms( httpRequestIdentificationDto , Key );

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-authenticate-key", Key );

        HttpResponseIdentificationDto responsedto = HttpResponseIdentificationDto.builder()
                .code(HttpStatus.OK.value())
                .message(SUCCESS_SEND_VERIFICATION_CODE_LITERAL).build();

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(responsedto);
    }

    @GetMapping("/api/identification")
    public ResponseEntity<HttpResponseIdentificationDto> identificationVerify(HttpServletRequest request, HttpServletResponse response, @RequestParam("identificationCode") String identificationCode ) throws JsonProcessingException {

        String Key = String.valueOf(request.getHeaders("x-authenticate-key"));

        if(Key == null){
            throw new NotIdentificationException(INVALID_AUTHENTICATION_CODE_LITERAL);
        }

        CertificationDto dto = CertificationDto.builder()
                .key(Key)
                .value(identificationCode)
                .build();

        HttpRequestIdentificationDto httpRequestIdentificationDto = smsService.verifySms(dto);
        HashMap<String,String> token = new HashMap<>();
        token.put("code", String.valueOf(httpRequestIdentificationDto.getCode()) );
        token.put("value", httpRequestIdentificationDto.getValue());

        String jwtToken = jwtUtil.createJwt(token,18000L,"Identification");

        HttpResponseIdentificationDto responseBody = HttpResponseIdentificationDto.builder().code(HttpStatus.OK.value()).message("인증 성공").build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION,jwtToken);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers) 
                .body(responseBody);
    }

}
