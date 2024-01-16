package com.ssg.usms.business.user.service;

import com.ssg.usms.business.user.Repository.UserRepository;
import com.ssg.usms.business.user.exception.AlreadyExistIdException;
import com.ssg.usms.business.user.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.user.persistence.HttpRequestSignUpDto;
import com.ssg.usms.business.user.persistence.UsmsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new AlreadyExistIdException("이미 존재하는 아이디입니다.");
        }
    }

    private void CheckDuplicatePhoneNumber(String phoneNumber) {

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new AlreadyExistPhoneNumException("이미 존재하는 전화번호입니다.");
        }
    }

}
