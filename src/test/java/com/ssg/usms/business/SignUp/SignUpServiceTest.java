package com.ssg.usms.business.SignUp;


import com.ssg.usms.business.login.Repository.UserRepository;
import com.ssg.usms.business.login.exception.AlreadyExistIdException;
import com.ssg.usms.business.login.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.login.persistence.HttpRequestSignUpDto;
import com.ssg.usms.business.login.persistence.UsmsUser;
import com.ssg.usms.business.login.service.SignUpService;
import com.ssg.usms.business.login.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.assertj.core.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SignUpServiceTest {
    private SignUpService service;

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserRepository repository;

    @BeforeEach
    public void setup() {
        service = new SignUpService(bCryptPasswordEncoder,repository);
    }

    @DisplayName("이미 중복된 아이디로 회원가입을 요청한 경우 AlreadyExistId에러를 날린다.")
    @Test
    public void testSinupWithAlreadyExistId(){
//        given
        given(repository.existsByUsername("aksenaksen")).willReturn(true);

        assertThrows(AlreadyExistIdException.class,() -> service.CheckDuplicatedId("aksenaksen"));
    }

    @DisplayName("이미 중복된 전화번호로 회원가입을 요청한 경우 AlreadyExistPhoneNum에러를 날린다.")
    @Test
    public void testSinupWithAlreadyExistPhoneNum(){
//        given
        given(repository.existsByPhoneNumber("010-1234-2323")).willReturn(true);

        assertThrows(AlreadyExistPhoneNumException.class,() -> service.CheckDuplicatePhoneNumber("010-1234-2323"));
    }

    @DisplayName("올바른 전화번호로 회원가입을 요청한 경우 duplicate 함수에서 false리턴")
    @Test
    public void testDuplicateWithValidPhoneNum(){
//        given
        given(repository.existsByPhoneNumber("010-1234-2323")).willReturn(false);
        boolean result = service.CheckDuplicatePhoneNumber("010-1234-2323");

        assertTrue(result);
    }

    @DisplayName("올바른 아이디로 회원가입을 요청한 경우 duplicate 함수에서 true리턴")
    @Test
    public void testDuplicateWithValidId(){
//        given
        given(repository.existsByUsername("aksenaksen")).willReturn(false);
        boolean result = service.CheckDuplicatedId("aksenaksen");

        assertTrue(result);
    }
//
//
@DisplayName("중복된 아이디를 요청한 경우 AlreadyExistIdException 을 던진다.")
@Test
public void testSinupWithDupicateId(){
//        given
    given(service.CheckDuplicatedId(any())).willThrow(new AlreadyExistIdException("이미 존재하는 아이디입니다."));

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
    public void testSinupWithDupicatePhoneNum(){
//        given
        given(service.CheckDuplicatePhoneNumber(any())).willThrow(new AlreadyExistPhoneNumException("이미 존재하는 전화번호입니다."));

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
    public void testSinupWithValidParam(){
//        given
        given(service.CheckDuplicatePhoneNumber(any())).willReturn(false);
        given(service.CheckDuplicatePhoneNumber(any())).willReturn(false);

        UsmsUser user= UsmsUser.builder()
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

        ResponseEntity<?> responseEntity = service.SignUp(dto); // 테스트 대상 메서드 호출
        // then
        Assertions.assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201); // 상태 코드가 200인지 검증
    }




}
