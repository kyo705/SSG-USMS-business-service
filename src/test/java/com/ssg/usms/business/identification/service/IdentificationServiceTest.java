package com.ssg.usms.business.identification.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.Identification.Service.IdentificationService;
import com.ssg.usms.business.Identification.dto.CertificationDto;
import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
import com.ssg.usms.business.Identification.dto.SmsCertificationDao;
import com.ssg.usms.business.Identification.error.NotIdentificationException;
import com.ssg.usms.business.notification.exception.NotificationFailureException;
import com.ssg.usms.business.notification.service.NotificationService;
import com.ssg.usms.business.notification.service.SmsNotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class IdentificationServiceTest {


    @Mock
    private SmsCertificationDao smsCertificationDao;
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Map<String,NotificationService> notificationService;

    @Mock
    private SmsNotificationService smsNotificationService;

    @InjectMocks
    private IdentificationService service;





    @DisplayName("인증번호 발송이 성공한 경우")
    @Test
    public void testSuccessSendVerificationCode() throws Exception {

        HttpRequestIdentificationDto requestdto = HttpRequestIdentificationDto.builder()
                .code(1)
                .value("010-4046-7715")
                .build();

        given(notificationService.get(any())).willReturn(smsNotificationService);

        service.createIdentification(requestdto,"1234");


        verify(smsCertificationDao, times(1)).createSmsCertification(any(),any());
    }



    @DisplayName("레디스에 키가 존재할때 HttpRequestIdentificationDto를 리턴해준다.")
    @Test
    public void testSuccessVerificationSms() throws Exception {

        HttpRequestIdentificationDto requestdto = HttpRequestIdentificationDto.builder()
                .code(1)
                .value("010-4046-7715")
                .build();

        CertificationDto dto = CertificationDto.builder()
                        .key("uuid")
                        .value("161321")
                        .build();

        when(smsCertificationDao.getSmsCertification(any())).thenReturn(requestdto.toString());
        when(objectMapper.readValue(requestdto.toString(), HttpRequestIdentificationDto.class)).thenReturn(new HttpRequestIdentificationDto());

        Object result = service.verifyIdentification(dto);

        assertThat(result).isInstanceOf(HttpRequestIdentificationDto.class);

        HttpRequestIdentificationDto resultDto = (HttpRequestIdentificationDto) result;

        assertThat(resultDto.getCode()).isEqualTo(resultDto.getCode());
        assertThat(resultDto.getValue()).isEqualTo(resultDto.getValue());
        verify(smsCertificationDao, times(1)).removeSmsCertification(dto.toString());

    }

    @DisplayName("redis에 해당하는 키와 벨류값이 없는경우 NotIdentifciationException에러 발생")
    @Test
    public void testFailVerify() throws Exception {

        CertificationDto requestdto = CertificationDto.builder()
                .key("hello motheasdf")
                .value("010-4046-7715")
                .build();

        when(smsCertificationDao.getSmsCertification(requestdto.toString())).thenReturn(null);

        assertThatThrownBy(() -> service.verifyIdentification(requestdto)).isInstanceOf(NotIdentificationException.class);
    }



    @DisplayName("NOTIFICATION 서비스들이 어떠한 문제로 전송에 실패한경우")
    @Test
    public void testFailSend() throws Exception {

        HttpRequestIdentificationDto requestdto = HttpRequestIdentificationDto.builder()
                .code(1)
                .value("010-4046-7715")
                .build();


        given(notificationService.get(any())).willReturn(smsNotificationService);
        doThrow(new NotificationFailureException("실패")).when(smsNotificationService).send(any(),any(),any());

        assertThatThrownBy(() -> service.createIdentification(requestdto,"1234")).isInstanceOf(NotificationFailureException.class);
    }






}
