package com.ssg.usms.business.identification.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.Identification.Service.SmsService;
import com.ssg.usms.business.Identification.Util.SmsUtil;
import com.ssg.usms.business.Identification.dto.CertificationDto;
import com.ssg.usms.business.Identification.dto.HttpRequestIdentificationDto;
import com.ssg.usms.business.Identification.dto.HttpResponseIdentificationDto;
import com.ssg.usms.business.Identification.dto.SmsCertificationDao;
import com.ssg.usms.business.Identification.error.NotIdentificationException;
import com.ssg.usms.business.user.service.SignUpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class IdentificationServiceTest {


    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SmsUtil smsUtil;

    private SmsService service;

    @Mock
    private SmsCertificationDao smsCertificationDao;

    @BeforeEach
    public void setup() {
        service = new SmsService(smsUtil,smsCertificationDao, objectMapper);
    }

    @DisplayName("인증번호 발송이 성공한 경우 x-authenticate-key와 함께 키가")
    @Test
    public void testSuccessSendVerificationCode() throws Exception {

        HttpRequestIdentificationDto requestdto = HttpRequestIdentificationDto.builder()
                .code(1)
                .value("010-4046-7715")
                .build();

        doNothing().when(smsCertificationDao).createSmsCertification(any(),any());

        assertTrue(service.sendSms(requestdto,"test"));
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

        Object result = service.verifySms(dto);

        assertThat(result).isInstanceOf(HttpRequestIdentificationDto.class);

        HttpRequestIdentificationDto resultDto = (HttpRequestIdentificationDto) result;

        assertThat(resultDto.getCode()).isEqualTo(resultDto.getCode());
        assertThat(resultDto.getValue()).isEqualTo(resultDto.getValue());
        verify(smsCertificationDao, times(1)).removeSmsCertification(dto.toString());

    }

    @DisplayName("인증번호를 통한 인증이 실패한경우")
    @Test
    public void testSuccessVerifySms() throws Exception {

        CertificationDto requestdto = CertificationDto.builder()
                .key("hello motheasdf")
                .value("010-4046-7715")
                .build();

        when(smsCertificationDao.getSmsCertification(requestdto.toString())).thenReturn(null);

        assertThatThrownBy(() -> service.verifySms(requestdto)).isInstanceOf(NotIdentificationException.class);
    }





}
