package com.ssg.usms.business.Identification.contorller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.usms.business.Identification.service.IdentificationService;
import com.ssg.usms.business.Identification.dto.CertificationDto;
import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
import com.ssg.usms.business.Identification.dto.HttpResponseIdentificationDto;
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
import javax.validation.Valid;
import java.util.HashMap;
import java.util.UUID;

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

    private final JwtUtil jwtUtil;

    @PostMapping("/api/identification")
    public ResponseEntity<HttpResponseIdentificationDto> identification(@Valid @RequestBody HttpRequestIdentificationDto httpRequestIdentificationDto) throws JsonProcessingException {

        String Key = UUID.randomUUID().toString();

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

        identificationService.createIdentification(httpRequestIdentificationDto,Key);

        HttpHeaders headers = new HttpHeaders();
        headers.add(IDENTIFICATION_HEADER, Key );

        HttpResponseIdentificationDto responsedto = HttpResponseIdentificationDto.builder()
                .code(HttpStatus.OK.value())
                .message(SUCCESS_SEND_VERIFICATION_CODE_LITERAL).build();

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(responsedto);
    }

    @GetMapping("/api/identification")
    public ResponseEntity<HttpResponseIdentificationDto> identificationVerify(HttpServletRequest request, @RequestParam("identificationCode") String identificationCode ) throws JsonProcessingException {

        String Key = String.valueOf(request.getHeaders(IDENTIFICATION_HEADER));

        if(Key == null){
            throw new NotIdentificationException(INVALID_AUTHENTICATION_CODE_LITERAL);
        }

        CertificationDto dto = CertificationDto.builder()
                .key(Key)
                .value(identificationCode)
                .build();

        HttpRequestIdentificationDto httpRequestIdentificationDto = identificationService.verifyIdentification(dto);

        HashMap<String,String> token = new HashMap<>();
        token.put("code", String.valueOf(httpRequestIdentificationDto.getCode()) );
        token.put("value", httpRequestIdentificationDto.getValue());

        String jwtToken = jwtUtil.createJwt(token,18000L,"Identification");

        HttpResponseIdentificationDto responseBody = HttpResponseIdentificationDto.builder().code(HttpStatus.OK.value()).message(IDENTIFICATION_SUCCESS_MESSAGE).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION,jwtToken);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers) 
                .body(responseBody);
    }

}
