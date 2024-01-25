package com.ssg.usms.business.user;


import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.user.dto.*;
import com.ssg.usms.business.user.exception.AlreadyExistEmailException;
import com.ssg.usms.business.user.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.user.exception.AlreadyExistUsernameException;
import com.ssg.usms.business.user.exception.NotExistingUserException;
import com.ssg.usms.business.user.repository.UserRepository;
import com.ssg.usms.business.user.repository.UserSessionRepository;
import com.ssg.usms.business.user.repository.UsmsUser;
import com.ssg.usms.business.user.service.UserService;
import com.ssg.usms.business.user.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository repository;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private UserService userService;

    @DisplayName("중복된 아이디를 요청한 경우 AlreadyExistUsernameException 을 던진다.")
    @Test
    public void testSinupWithDupicateId() {

        given(repository.existsByUsername(any())).willReturn(true);

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("httpRequestSign");
        dto.setPassword("hashedpassword123@");
        dto.setPhoneNum("010-1234-24124");
        dto.setEmail("asdf123@naer.com");
        dto.setNickname("hihello");


        assertThrows(AlreadyExistUsernameException.class, () -> userService.signUp(dto));
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


        assertThrows(AlreadyExistPhoneNumException.class, () -> userService.signUp(dto));
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

        HttpRequestSignUpDto dto = new HttpRequestSignUpDto();
        dto.setUsername("httpRequestSign");
        dto.setPassword("hashedpassword123@");
        dto.setPhoneNum("010-1234-24124");
        dto.setEmail("asdf123@naer.com");
        dto.setNickname("hihello");
        // then
        assertThatCode(() -> userService.signUp(dto))
                .doesNotThrowAnyException();
    }






    @DisplayName("인자로 들어온 token값으로 성공적으로 유저를 찾아서 HttpResponseDto를 리턴")
    @Test
    public void SuccessFindUserByValueCode0(){

        UsmsUser user = UsmsUser.builder()
                .username("httpRequestSign")
                .password("hashedpassword123@")
                .personName("hihello")
                .phoneNumber("010-1234-24124")
                .email("asdf123@naer.com")
                .id(1L)
                .build();

        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .personName(user.getPersonName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .securityState(user.getSecurityState())
                .build();

        Claims fakeClaims = Jwts.claims()
                .add("code", "0")
                .add("value", "asdf123@naer.com")
                .build();

        given(jwtUtil.getClaim(any())).willReturn(fakeClaims);

        given(repository.findByEmail(any())).willReturn(user);

        Assertions.assertThat(userService.findUserByValue("").toString()).isEqualTo(dto.toString());
    }

    @DisplayName("인자로 들어온 token값으로 user를 찾을수 없는 경우")
    @Test
    public void FailedFindUserByCode0(){

        UsmsUser user = UsmsUser.builder()
                .username("httpRequestSign")
                .password("hashedpassword123@")
                .personName("hihello")
                .phoneNumber("010-1234-24124")
                .email("asdf123@naer.com")
                .id(1L)
                .build();

        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .personName(user.getPersonName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .securityState(user.getSecurityState())
                .build();

        Claims fakeClaims = Jwts.claims()
                .add("code", "0")
                .add("value", "asdf123@naer.com")
                .build();

        given(jwtUtil.getClaim(any())).willReturn(fakeClaims);

        given(repository.findByEmail(any())).willReturn(null);

        assertThrows(NotExistingUserException.class ,() -> userService.findUserByValue("").toString());
    }



    @DisplayName("인자로 들어온 token값으로 성공적으로 유저를 찾아서 HttpResponseDto를 리턴")
    @Test
    public void SuccessFindUserByValue(){

        UsmsUser user = UsmsUser.builder()
                .username("httpRequestSign")
                .password("hashedpassword123@")
                .personName("hihello")
                .phoneNumber("010-1234-24124")
                .email("asdf123@naer.com")
                .id(1L)
                .build();

        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .personName(user.getPersonName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .securityState(user.getSecurityState())
                .build();

        Claims fakeClaims = Jwts.claims()
                .add("code", "1")
                .add("value", "010-1234-5124")
                .build();

        given(jwtUtil.getClaim(any())).willReturn(fakeClaims);

        given(repository.findByPhoneNumber(any())).willReturn(user);

        Assertions.assertThat(userService.findUserByValue("").toString()).isEqualTo(dto.toString());
    }

    @DisplayName("인자로 들어온 token값으로 user를 찾을수 없는 경우")
    @Test
    public void FailedFindUserByCode1(){

        UsmsUser user = UsmsUser.builder()
                .username("httpRequestSign")
                .password("hashedpassword123@")
                .personName("hihello")
                .phoneNumber("010-1234-24124")
                .email("asdf123@naer.com")
                .id(1L)
                .build();

        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .personName(user.getPersonName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .securityState(user.getSecurityState())
                .build();

        Claims fakeClaims = Jwts.claims()
                .add("code", "1")
                .add("value", "010-1234-5124")
                .build();

        given(jwtUtil.getClaim(any())).willReturn(fakeClaims);

        given(repository.findByPhoneNumber(any())).willReturn(null);

        assertThrows(NotExistingUserException.class ,() -> userService.findUserByValue("").toString());
    }


    @DisplayName("성공적으로 수정기능이 작동한 경우 UsmsUser을 리턴한다.")
    @Test
    public void SuccessModifyUser(){
        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("hihello")
                .email("asdf123@naer.com")
                .phoneNumber("010-1234-2412")
                .password("hashedpassword123@")
                .securityState(SecurityState.BASIC)
                .build();

        UsmsUser User = new UsmsUser();
        User.setId(1L);
        User.setPersonName("tmpName");
        User.setEmail("tmp123@naver.com");
        User.setPhoneNumber("010-1234-4242");
        User.setPassword("tmpPassword");
        User.setSecurityState(SecurityState.BASIC);
        User.setSecondPassword(null);

        UsmsUser newUser = new UsmsUser();
        newUser.setId(1L);
        newUser.setPersonName(dto.getPersonName());
        newUser.setEmail(dto.getEmail());
        newUser.setPhoneNumber(dto.getPhoneNumber());
        newUser.setPassword(dto.getPassword());
        newUser.setSecurityState(dto.getSecurityState());
        newUser.setSecondPassword(User.getSecondPassword());

        given(bCryptPasswordEncoder.encode(any())).willReturn(dto.getPassword());
        given(repository.findById(1L)).willReturn(Optional.of(User));

        Assertions.assertThat(userService.modifyUser(1L,dto)).isInstanceOf(UsmsUser.class);
        Assertions.assertThat(userService.modifyUser(1L,dto).toString()).isEqualTo(newUser.toString());
    }

    @DisplayName("ModifyUserDto에 필드값이 다 들어가지 않아도 성공적으로 수정기능이 작동한 경우 UsmsUser을 리턴한다.(이 경우에는 비밀번호를 제외했다)")
    @Test
    public void SuccessModifyUserWithsomeArgforDto(){
        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("hihello")
                .email("asdf123@naer.com")
                .phoneNumber("010-1234-2412")
                .securityState(SecurityState.BASIC)
                .build();

        UsmsUser User = new UsmsUser();
        User.setId(1L);
        User.setPersonName("tmpName");
        User.setEmail("tmp123@naver.com");
        User.setPhoneNumber("010-1234-4242");
        User.setPassword("tmpPassword");
        User.setSecurityState(SecurityState.BASIC);
        User.setSecondPassword(null);

        UsmsUser newUser = new UsmsUser();
        newUser.setId(1L);
        newUser.setPersonName(dto.getPersonName());
        newUser.setEmail(dto.getEmail());
        newUser.setPhoneNumber(dto.getPhoneNumber());
        newUser.setPassword(User.getPassword());
        newUser.setSecurityState(dto.getSecurityState());
        newUser.setSecondPassword(User.getSecondPassword());

        given(repository.findById(1L)).willReturn(Optional.of(User));

        Assertions.assertThat(userService.modifyUser(1L,dto)).isInstanceOf(UsmsUser.class);
        Assertions.assertThat(userService.modifyUser(1L,dto).toString()).isEqualTo(newUser.toString());
    }



    @DisplayName("들어온 값으로 유저정보를 찾을수 없는 경우 IllagalArgumentException을 던진다.")
    @Test
    public void FailedModifyUserWithNotAllowedValue(){


        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("hihello")
                .email("asdf123@naer.com")
                .phoneNumber("010-1234-2412")
                .password("hashedpassword123@")
                .securityState(SecurityState.BASIC)
                .build();

        given(repository.findById(1L)).willReturn(Optional.ofNullable(any()));
        assertThrows(IllegalArgumentException.class , () -> userService.modifyUser(1L,dto));
    }


    @DisplayName("세션이 존재할때 세션을통해서 유저정보를 가져와서 dto를 반환한다.")
    @Test
    public void testFindUserdtoWithSession() throws Exception{

        UsmsUser user = UsmsUser.builder()
                .id(1L)
                .username("tkfka123")
                .password("asdfasdf2312*")
                .email("email@naver.com")
                .personName("hongu")
                .phoneNumber("010-1234-4544")
                .isLock(false)
                .securityState(SecurityState.BASIC)
                .role(UserRole.ROLE_STORE_OWNER)
                .build();

        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .personName(user.getPersonName())
                .phoneNumber(user.getPhoneNumber())
                .securityState(user.getSecurityState())
                .email(user.getEmail())
                .build();

        UsmsUserDetails userDetails = new UsmsUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);


        Assertions.assertThat(userService.findUserBySession().toString()).isEqualTo(dto.toString());
    }

    @DisplayName("세션 없이 유저 정보 조회를 시도할 시 에러 발생")
    @Test
    public void testFindUserdtoNoAccessSession(){

        String principal = "aksen";
        Authentication authentication = new AnonymousAuthenticationToken(principal, principal, List.of(()-> "ROLE_ANONYMOUS"));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        assertThrows(IllegalAccessException.class, () -> userService.findUserBySession());
    }


    @DisplayName("현재 세션에 해당하는 유저의 세션으로 유저정보 삭제를 요청했을때 세션 삭제 및 유저 삭제")
    @Test
    public void testDeleteUser() throws Exception{

        UsmsUser user = UsmsUser.builder()
                .id(1L)
                .username("tkfka123")
                .password("asdfasdf2312*")
                .email("email@naver.com")
                .personName("hongu")
                .phoneNumber("010-1234-4544")
                .isLock(false)
                .securityState(SecurityState.BASIC)
                .role(UserRole.ROLE_STORE_OWNER)
                .build();

        UsmsUserDetails userDetails = new UsmsUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        userService.deleteUser(user.getId());

        verify(userSessionRepository,times(1)).deleteSession(user.getUsername());
        verify(repository,times(1)).delete(anyLong());

    }

    @DisplayName("존재하지 않는 정보로 중복확인을 한 경우")
    @Test
    public void checkExistUserSuccess(){
        String username = "hello123";
        String email = "ver.com";
        String phone = "010-14244-1324";

        given(repository.existsByEmail(any())).willReturn(false);
        given(repository.existsByPhoneNumber(any())).willReturn(false);
        given(repository.existsByUsername(any())).willReturn(false);

        assertDoesNotThrow(() -> userService.checkExistUser(username,email,phone));

    }
    @DisplayName("존재하지 않는 정보로 중복확인을 한 경우 2파라미터")
    @Test
    public void checkExistUserSuccesstwoParam(){
        String username = "hello123";
        String email = "ver.com";
        String phone = null;

        given(repository.existsByEmail(any())).willReturn(false);
        given(repository.existsByUsername(any())).willReturn(false);

        assertDoesNotThrow(() -> userService.checkExistUser(username,email,phone));

    }

    @DisplayName("존재하는 정보로 중복확인을 한 경우")
    @Test
    public void checkExistUserFailedwithduplicateUsername(){
        String username = "hello123";
        String email = "ver.com";
        String phone = null;

        given(repository.existsByEmail(any())).willReturn(false);
        given(repository.existsByUsername(any())).willReturn(true);

        assertThrows(AlreadyExistUsernameException.class , () -> userService.checkExistUser(username,email,phone));
    }

    @DisplayName("존재하는 정보로 중복확인을 한 경우")
    @Test
    public void checkExistUserFailedwithduplicateEmail(){
        String phone = null;
        String username = "hello123";
        String email = "ver.com";

        given(repository.existsByEmail(any())).willReturn(true);

        assertThrows(AlreadyExistEmailException.class , () -> userService.checkExistUser(username,email,phone));
    }

    @DisplayName("존재하는 정보로 중복확인을 한 경우")
    @Test
    public void checkExistUserFailedwithduplicatePhoneNum(){
        String username = "hello123";
        String email = "ver.com";
        String phone = "010-14244-1324";

        given(repository.existsByPhoneNumber(any())).willReturn(true);

        assertThrows(AlreadyExistPhoneNumException.class , () -> userService.checkExistUser(username,email,phone));
    }

}
