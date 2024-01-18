package com.ssg.usms.business.user.service;

import com.ssg.usms.business.user.Repository.UserRepository;
import com.ssg.usms.business.user.exception.AlreadyExistIdException;
import com.ssg.usms.business.user.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.user.dto.HttpRequestSignUpDto;
import com.ssg.usms.business.user.Repository.UsmsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ssg.usms.business.user.constant.UserConstants.ALREADY_EXISTS_ID_LITERAL;
import static com.ssg.usms.business.user.constant.UserConstants.ALREADY_EXISTS_PHONE_LITERAL;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    @Transactional
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

        userRepository.save(user);
    }


    private void CheckDuplicatedId(String username) {

        if (userRepository.existsByUsername(username)) {
            throw new AlreadyExistIdException(ALREADY_EXISTS_ID_LITERAL);
        }
    }

    private void CheckDuplicatePhoneNumber(String phoneNumber) {

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new AlreadyExistPhoneNumException(ALREADY_EXISTS_PHONE_LITERAL);
        }
    }

}
