package com.ssg.usms.business.Identification.Service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.Identification.dto.CertificationDto;
import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
import com.ssg.usms.business.Identification.dto.SmsCertificationDao;
import com.ssg.usms.business.Identification.error.NotIdentificationException;
import com.ssg.usms.business.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.ssg.usms.business.Identification.constant.IdenticationConstant.IDENTIFICATION_SUBJECT;
import static com.ssg.usms.business.Identification.constant.IdenticationConstant.INVALID_AUTHENTICATION_CODE_LITERAL;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class IdentificationService {

    private final Map<String , NotificationService> notificationServices;

    private final SmsCertificationDao smsCertificationDao;
    private final ObjectMapper objectMapper;

    public void createIdentification(HttpRequestIdentificationDto dto,String verificationKey) throws JsonProcessingException {

        String User = objectMapper.writeValueAsString(dto);
        String verificationCode = makeVerificationCode();
        String redisValue = verificationKey + " " + verificationCode;

        smsCertificationDao.createSmsCertification(redisValue, User);
        String serviceName = makeNotificationServiceValue(dto.getCode());

        notificationServices.get(serviceName).send(dto.getValue(),IDENTIFICATION_SUBJECT,verificationCode);
    }

    public HttpRequestIdentificationDto verifyIdentification(CertificationDto dto) throws JsonProcessingException {

        String verifyKey = dto.toString();
        String result = smsCertificationDao.getSmsCertification(dto.toString());

        if( result == null ){
            throw new NotIdentificationException(INVALID_AUTHENTICATION_CODE_LITERAL);
        }

        smsCertificationDao.removeSmsCertification(verifyKey);

        return objectMapper.readValue(result, HttpRequestIdentificationDto.class);
    }

    private String makeVerificationCode(){

        int randomNumber = (int) (Math.random() * 900_000) + 100_000;
        return String.valueOf(randomNumber);
    }

    private String makeNotificationServiceValue(int code){

        if(code == 0){ return "emailNotificationService";}
        return "smsNotificationService";
    }


}
