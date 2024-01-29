package com.ssg.usms.business.user.service;

import com.ssg.usms.business.Identification.dto.CertificationCode;
import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.user.dto.HttpRequestModifyUserDto;
import com.ssg.usms.business.user.dto.HttpRequestSignUpDto;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import com.ssg.usms.business.user.exception.AlreadyExistEmailException;
import com.ssg.usms.business.user.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.user.exception.AlreadyExistUsernameException;
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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ssg.usms.business.Identification.constant.IdentificationConstant.IDENTIFICATION_CODE;
import static com.ssg.usms.business.Identification.constant.IdentificationConstant.IDENTIFICATION_VALUE;
import static com.ssg.usms.business.user.constant.UserConstants.*;

@Service
@RequiredArgsConstructor
public class UserService {


    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    //   유저 데이터 조회
    @Transactional
    public void signUp(HttpRequestSignUpDto httpRequestSignUpDto) {

        checkDuplicatedId(httpRequestSignUpDto.getUsername());
        checkDuplicatePhoneNumber(httpRequestSignUpDto.getPhoneNumber());


        String hashPwd = bCryptPasswordEncoder.encode(httpRequestSignUpDto.getPassword());
        UsmsUser user = UsmsUser.builder()
                .username(httpRequestSignUpDto.getUsername())
                .password(hashPwd)
                .nickname(httpRequestSignUpDto.getNickname())
                .phoneNumber(httpRequestSignUpDto.getPhoneNumber())
                .email(httpRequestSignUpDto.getEmail())
                .build();

        userRepository.signUp(user);
    }
    public List<HttpResponseUserDto> findUserByValue(String token) {

        int code = Integer.parseInt((String) jwtUtil.getClaim(token).get(IDENTIFICATION_CODE));
        String value = (String) jwtUtil.getClaim(token).get(IDENTIFICATION_VALUE);
        List<UsmsUser> userList = null;

        if (code == CertificationCode.EMAIL.getCode()) {
            userList = userRepository.findAllByEmail(value);
        }
        if (code == CertificationCode.SMS.getCode()) {
            userList = userRepository.findAllByPhoneNumber(value);
        }
        if (Objects.requireNonNull(userList).isEmpty()) {
            throw new NotExistingUserException(NOT_EXISTING_USER_IN_SESSION_LITERAL);
        }


        return userList.stream()
                .map(user -> HttpResponseUserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .securityState(user.getSecurityState().getLevel())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public UsmsUser modifyUser(long value, HttpRequestModifyUserDto httpRequestModifyUserDto) {

        UsmsUser user = userRepository.findById(value).orElseThrow(() -> new IllegalArgumentException(NOT_EXISTING_USER_IN_SESSION_LITERAL));

        if (httpRequestModifyUserDto.getNickname() != null) {
            user.setNickname(httpRequestModifyUserDto.getNickname());
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
                    .nickname(userDetails.getPersonName())
                    .phoneNumber(userDetails.getPhoneNumber())
                    .securityState(userDetails.getSecurityState().getLevel())
                    .email(userDetails.getEmail())
                    .build();

            return dto;

        } catch (NullPointerException | ClassCastException e) {
            throw new IllegalAccessException(NOT_ALLOWED_SESSION_LITERAL);
        }

    }


    @Transactional
    public void deleteUser(long userId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userSessionRepository.deleteSession(username);
        userRepository.delete(userId);
    }


    public void checkExistUser(String username,String email, String phoneNumber) {

        if (email != null){

            if(userRepository.existsByEmail(email)){
                throw new AlreadyExistEmailException(ALREADY_EXISTS_USER_LITERAL);
            };
        }if (phoneNumber != null){

            if(userRepository.existsByPhoneNumber(phoneNumber)){
                throw new AlreadyExistPhoneNumException(ALREADY_EXISTS_USER_LITERAL);
            };
        }if (username != null){

            if(userRepository.existsByUsername(username)){

                throw new AlreadyExistUsernameException(ALREADY_EXISTS_USER_LITERAL);
            };
        }
    }

    private void checkDuplicatedId(String username) {

        if (userRepository.existsByUsername(username)) {
            throw new AlreadyExistUsernameException(ALREADY_EXISTS_USERNAME_LITERAL);
        }
    }

    private void checkDuplicatePhoneNumber(String phoneNumber) {

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new AlreadyExistPhoneNumException(ALREADY_EXISTS_PHONE_LITERAL);
        }
    }
}
