package com.ssg.usms.business.user.service;

import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.user.dto.HttpRequestModifyUserDto;
import com.ssg.usms.business.user.dto.HttpRequestSignUpDto;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import com.ssg.usms.business.user.exception.AlreadyExistIdException;
import com.ssg.usms.business.user.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.user.exception.NotExistingUserException;
import com.ssg.usms.business.user.repository.UserRepository;
import com.ssg.usms.business.user.repository.UserSessionRepository;
import com.ssg.usms.business.user.repository.UsmsUser;
import com.ssg.usms.business.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ssg.usms.business.user.constant.UserConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {


    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    //   유저 데이터 조회
    public void SignUp(HttpRequestSignUpDto httpRequestSignUpDto) {

        CheckDuplicatedId(httpRequestSignUpDto.getUsername());
        CheckDuplicatePhoneNumber(httpRequestSignUpDto.getPhoneNum());

        String hashPwd = bCryptPasswordEncoder.encode(httpRequestSignUpDto.getPassword());
        UsmsUser user = UsmsUser.builder()
                .username(httpRequestSignUpDto.getUsername())
                .password(hashPwd)
                .personName(httpRequestSignUpDto.getNickname())
                .phoneNumber(httpRequestSignUpDto.getPhoneNum())
                .email(httpRequestSignUpDto.getEmail())
                .build();

        userRepository.signUp(user);
    }


    private void CheckDuplicatedId(String username) {

        if (userRepository.existsByUsername(username)) {
            throw new AlreadyExistIdException(ALREADY_EXISTS_USERNAME_LITERAL);
        }
    }

    private void CheckDuplicatePhoneNumber(String phoneNumber) {

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new AlreadyExistPhoneNumException(ALREADY_EXISTS_PHONE_LITERAL);
        }
    }


    public HttpResponseUserDto findUserByValue(String token) {

        int code = Integer.parseInt((String) jwtUtil.getClaim(token).get("code"));
        String value = (String) jwtUtil.getClaim(token).get("value");
        UsmsUser user = null;

        if (code == 0) {
            user = userRepository.findByEmail(value);
        }
        if (code == 1) {
            user = userRepository.findByPhoneNumber(value);
        }
        if (user == null) {
            throw new NotExistingUserException("존재하지 않는 유저 정보입니다.");
        }

        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .personName(user.getPersonName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .securityState(user.getSecurityState())
                .build();

        return dto;
    }

    @Transactional
    public UsmsUser ModifyUser(long value, HttpRequestModifyUserDto httpRequestModifyUserDto) {

        UsmsUser user = userRepository.findById(value).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (httpRequestModifyUserDto.getPersonName() != null) {
            user.setPersonName(httpRequestModifyUserDto.getPersonName());
        }

        if (httpRequestModifyUserDto.getEmail() != null) {
            user.setEmail(httpRequestModifyUserDto.getEmail());
        }

        if (httpRequestModifyUserDto.getPhoneNumber() != null) {
            user.setPhoneNumber(httpRequestModifyUserDto.getPhoneNumber());
        }

        if (httpRequestModifyUserDto.getPassword() != null) {
            user.setPassword(bCryptPasswordEncoder.encode(httpRequestModifyUserDto.getPassword()));
        }

        if (httpRequestModifyUserDto.getSecurityState() != null) {
            user.setSecurityState(httpRequestModifyUserDto.getSecurityState());
        }

        if (httpRequestModifyUserDto.getSecondPassword() != null) {
            user.setSecondPassword(httpRequestModifyUserDto.getSecondPassword());
        }

        return user;
    }

    public HttpResponseUserDto findUserBySession() throws IllegalAccessException {

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsmsUserDetails userDetails = (UsmsUserDetails) authentication.getPrincipal();

            if (userDetails == null) {
                throw new IllegalAccessException(NOT_ALLOWED_SESSION_LITERAL);
            }

            HttpResponseUserDto dto = HttpResponseUserDto.builder()
                    .id(userDetails.getId())
                    .username(userDetails.getUsername())
                    .personName(userDetails.getPersonName())
                    .phoneNumber(userDetails.getPhoneNumber())
                    .securityState(userDetails.getSecurityState())
                    .email(userDetails.getEmail())
                    .build();

            return dto;

        } catch (NullPointerException | ClassCastException e) {
            throw new IllegalAccessException(NOT_ALLOWED_SESSION_LITERAL);
        }

    }


    public void deleteUser(long userId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userSessionRepository.deleteSession(username);
        userRepository.delete(userId);
    }


}
