package com.ssg.usms.business.user.session;


import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import com.ssg.usms.business.user.dto.SecurityState;
import com.ssg.usms.business.user.dto.UserRole;
import com.ssg.usms.business.user.repository.UserRepository;
import com.ssg.usms.business.user.repository.UserSessionRepository;
import com.ssg.usms.business.user.repository.UsmsUser;
import com.ssg.usms.business.user.service.UserSessionService;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserSessionServiceTest {

    @InjectMocks
    private UserSessionService userSessionService;
    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private UserRepository userRepository;

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


        assertThat(userSessionService.findUserBySession().toString()).isEqualTo(dto.toString());
    }

    @DisplayName("세션 없이 유저 정보 조회를 시도할 시 에러 발생")
    @Test
    public void testFindUserdtoNoAccessSession(){

        String principal = "aksen";
        Authentication authentication = new AnonymousAuthenticationToken(principal, principal, List.of(()-> "ROLE_ANONYMOUS"));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        assertThrows(IllegalAccessException.class, () -> userSessionService.findUserBySession());
    }

//    @DisplayName("세션없이 유저 정보 조회를 시도할 시 에러 발생")
//    @Test
//    public void testFindUserdtoNoSession(){
//
//        assertThrows(IllegalAccessException.class, () -> userSessionService.findUserBySession());
//    }

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

        userSessionService.deleteUser(user.getId());

       verify(userSessionRepository,times(1)).deleteSession(user.getUsername());
       verify(userRepository,times(1)).delete(any());

    }

    @DisplayName("허용되지않은 세션으로 유저정보를 삭제하려고 할때 에러 발생")
    @Test
    public void testDeleteUserdtoNoAccessSession() throws IllegalAccessException {

        String principal = "aksen";
        Authentication authentication = new AnonymousAuthenticationToken(principal, principal, List.of(()-> "ROLE_ANONYMOUS"));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        assertThrows(IllegalAccessException.class, () -> userSessionService.deleteUser(1L));
        verify(userSessionRepository,times(0)).deleteSession(any());
        verify(userRepository,times(0)).delete(any());
    }

}
