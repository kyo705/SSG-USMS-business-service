package com.ssg.usms.business.Identification.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.Identification.dto.CertificationDto;
import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
import com.ssg.usms.business.Identification.repository.IdentificationRepository;
import com.ssg.usms.business.Identification.error.NotIdentificationException;
import com.ssg.usms.business.notification.service.NotificationService;
import com.ssg.usms.business.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ssg.usms.business.Identification.constant.IdenticationConstant.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class IdentificationService {

    private final Map<String , NotificationService> notificationServices;

    private final JwtUtil jwtUtil;

    private final IdentificationRepository identificationRepository;
    private final ObjectMapper objectMapper;

    public String createIdentification(HttpRequestIdentificationDto dto) throws JsonProcessingException {

        String verificationKey = UUID.randomUUID().toString();

        String User = objectMapper.writeValueAsString(dto);
        String verificationCode = makeVerificationCode();
        String redisValue = verificationKey + " " + verificationCode;

        identificationRepository.createSmsCertification(redisValue, User);
        String serviceName = makeNotificationServiceValue(dto.getCode());
        notificationServices.get(serviceName).send(dto.getValue(),IDENTIFICATION_SUBJECT,verificationCode);

        return verificationKey;
    }

    public String verifyIdentification(CertificationDto dto) throws JsonProcessingException {

        String verifyKey = dto.getKey()+" "+dto.getValue();
        String result = identificationRepository.getSmsCertification(verifyKey);

        if( result == null ){
            throw new NotIdentificationException(INVALID_AUTHENTICATION_CODE_LITERAL);
        }

        identificationRepository.removeSmsCertification(verifyKey);

        HttpRequestIdentificationDto httpRequestIdentificationDto = objectMapper.readValue(result, HttpRequestIdentificationDto.class);;

        HashMap<String,String> token = new HashMap<>();
        token.put("code", String.valueOf(httpRequestIdentificationDto.getCode()) );
        token.put("value", httpRequestIdentificationDto.getValue());

        String jwtToken = jwtUtil.createJwt(token,18000L,"Identification");

        return jwtToken;
    }

    private String makeVerificationCode(){

        int randomNumber = (int) (Math.random() * 900_000) + 100_000;
        return String.valueOf(randomNumber);
    }

    private String makeNotificationServiceValue(int code){

        return NOTIFICATION_SERVICE_MAP.get(code);
    }


}
