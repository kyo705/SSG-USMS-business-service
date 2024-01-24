package com.ssg.usms.business.identification.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.Identification.service.IdentificationService;
import com.ssg.usms.business.Identification.dto.CertificationDto;
import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
import com.ssg.usms.business.Identification.repository.IdentificationRepository;
import com.ssg.usms.business.Identification.error.NotIdentificationException;
import com.ssg.usms.business.notification.exception.NotificationFailureException;
import com.ssg.usms.business.notification.service.NotificationService;
import com.ssg.usms.business.notification.service.SmsNotificationService;
import com.ssg.usms.business.user.util.JwtUtil;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class IdentificationServiceTest {


    @Mock
    private IdentificationRepository identificationRepository;
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Map<String,NotificationService> notificationService;

    @Mock
    private SmsNotificationService smsNotificationService;

    @InjectMocks
    private IdentificationService service;

    @Mock
    private JwtUtil jwtUtil;





    @DisplayName("인증번호 발송이 성공한 경우")
    @Test
    public void testSuccessSendVerificationCode() throws Exception {

        HttpRequestIdentificationDto requestdto = HttpRequestIdentificationDto.builder()
                .code(1)
                .value("010-4046-7715")
                .build();

        given(notificationService.get(any())).willReturn(smsNotificationService);

        service.createIdentification(requestdto);


        verify(identificationRepository, times(1)).createSmsCertification(any(),any());
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

        when(identificationRepository.getIdentification(any())).thenReturn(requestdto.toString());
        when(objectMapper.readValue(requestdto.toString(), HttpRequestIdentificationDto.class)).thenReturn(new HttpRequestIdentificationDto());
        given(jwtUtil.createJwt(any(),any(),any())).willReturn("asdf");

        String result = service.verifyIdentification(dto);
        String Key = dto.getKey()+" "+dto.getValue();


        assertThat(result).isEqualTo("asdf");
        verify(identificationRepository, times(1)).removeIdentification(Key);

    }

    @DisplayName("redis에 해당하는 키와 벨류값이 없는경우 NotIdentifciationException에러 발생")
    @Test
    public void testFailVerify() throws Exception {

        CertificationDto requestdto = CertificationDto.builder()
                .key("hello motheasdf")
                .value("010-4046-7715")
                .build();

        given(identificationRepository.getIdentification(any())).willReturn(null);

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

        assertThatThrownBy(() -> service.createIdentification(requestdto)).isInstanceOf(NotificationFailureException.class);
    }






}
