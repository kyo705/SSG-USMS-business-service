package com.ssg.usms.business.User.service;

import com.ssg.usms.business.User.Repository.UserRepository;
import com.ssg.usms.business.User.exception.AlreadyExistIdException;
import com.ssg.usms.business.User.exception.AlreadyExistPhoneNumException;
import com.ssg.usms.business.User.persistence.HttpRequestSignUpDto;
import com.ssg.usms.business.User.persistence.UsmsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    public boolean SignUp(HttpRequestSignUpDto httpRequestSignUpDto) {

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

        UsmsUser SavedUser = userRepository.save(user);

        if (SavedUser.getId() > 0) {

            return true;
        }

        return false;
    }

    public boolean CheckDuplicatedId(String username) {

        if (userRepository.existsByUsername(username)) {
            throw new AlreadyExistIdException("이미 존재하는 아이디입니다.");
        }

        return true;
    }

    public boolean CheckDuplicatePhoneNumber(String phoneNumber) {

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new AlreadyExistPhoneNumException("이미 존재하는 전화번호입니다.");
        }

        return true;
    }

}
