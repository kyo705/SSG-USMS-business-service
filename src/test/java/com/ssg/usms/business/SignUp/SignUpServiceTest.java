package com.ssg.usms.business.SignUp;


import com.ssg.usms.business.user.Repository.UserRepository;
import com.ssg.usms.business.user.exception.AlreadyExistIdException;
import com.ssg.usms.business.user.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.user.persistence.HttpRequestSignUpDto;
import com.ssg.usms.business.user.persistence.UsmsUser;
import com.ssg.usms.business.user.service.SignUpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@ActiveProfiles("test")
@SpringBootTest
public class SignUpServiceTest {
    private SignUpService service;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserRepository repository;

    @BeforeEach
    public void setup() {
        service = new SignUpService(bCryptPasswordEncoder, repository);
    }

    @DisplayName("중복된 아이디를 요청한 경우 AlreadyExistIdException 을 던진다.")
    @Test
    public void testSinupWithDupicateId() {

        given(repository.existsByUsername(any())).willReturn(true);

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("httpRequestSign");
        dto.setPassword("hashedpassword123@");
        dto.setPhoneNum("010-1234-24124");
        dto.setEmail("asdf123@naer.com");
        dto.setNickname("hihello");


        assertThrows(AlreadyExistIdException.class, () -> service.SignUp(dto));
    }

    @DisplayName("중복된 전화번호를 요청한 경우 AlreadyExistPhoneNumbException 을 던진다..")
    @Test
    public void testSinupWithDupicatePhoneNum() {

        given(repository.existsByPhoneNumber(any())).willReturn(true);

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("httpRequestSign");
        dto.setPassword("hashedpassword123@");
        dto.setPhoneNum("010-1234-24124");
        dto.setEmail("asdf123@naer.com");
        dto.setNickname("hihello");


        assertThrows(AlreadyExistPhoneNumException.class, () -> service.SignUp(dto));
    }

    @DisplayName("올바른 값을 요청한 경우 200 ResponseEntity를 반환한다.")
    @Test
    public void testSinupWithValidParam() {

        UsmsUser user = UsmsUser.builder()
                .username("httpRequestSign")
                .password("hashedpassword123@")
                .personName("hihello")
                .phoneNumber("010-1234-24124")
                .email("asdf123@naer.com")
                .id(1L)
                .build();

        given(repository.save(any())).willReturn(user);
        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("httpRequestSign");
        dto.setPassword("hashedpassword123@");
        dto.setPhoneNum("010-1234-24124");
        dto.setEmail("asdf123@naer.com");
        dto.setNickname("hihello");
        // then
        assertThatCode(() -> service.SignUp(dto))
                .doesNotThrowAnyException();
    }
}
