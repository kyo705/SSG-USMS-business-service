package com.ssg.usms.business.Identification.Service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
import com.ssg.usms.business.Identification.dto.CertificationDto;
import com.ssg.usms.business.Identification.dto.SmsCertificationDao;
import com.ssg.usms.business.Identification.Util.SmsUtil;
import com.ssg.usms.business.Identification.error.NotIdentificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ssg.usms.business.Identification.constant.IdenticationConstant.INVALID_AUTHENTICATION_CODE_LITERAL;

@Service
@Transactional
@RequiredArgsConstructor
public class SmsService {

    private final SmsUtil smsutil;
    private final SmsCertificationDao smsCertificationDao;
    private final ObjectMapper objectMapper;
    public boolean sendSms(HttpRequestIdentificationDto dto,String verificationKey) throws JsonProcessingException {

        int code = dto.getCode();
        String value = dto.getValue().replaceAll("-","");
        String verificationCode = makeVerificationCode();
        String redisValue =verificationKey+" "+verificationCode;
        String User = objectMapper.writeValueAsString(dto);

        if (code == 0){

            return false;
        }
        if(code == 1){

            smsCertificationDao.createSmsCertification( redisValue , User);
            smsutil.sendOne(value,verificationCode);
            return true;
        }

        return false;
    }

    public HttpRequestIdentificationDto verifySms(CertificationDto dto) throws JsonProcessingException {

        String verifyKey = dto.toString();
        String result = getValue(verifyKey);

        if( result == null ){
            throw new NotIdentificationException(INVALID_AUTHENTICATION_CODE_LITERAL);
        }

        smsCertificationDao.removeSmsCertification(verifyKey);

        return objectMapper.readValue(result, HttpRequestIdentificationDto.class);
    }

    private String getValue(String Key){

        return smsCertificationDao.getSmsCertification(Key);
    }

    private String makeVerificationCode(){

        int randomNumber = (int) (Math.random() * 900_000) + 100_000;
        return String.valueOf(randomNumber);
    }

}
