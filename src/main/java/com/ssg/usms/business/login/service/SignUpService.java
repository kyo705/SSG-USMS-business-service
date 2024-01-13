package com.ssg.usms.business.login.service;

import com.ssg.usms.business.login.exception.*;
import com.ssg.usms.business.login.persistence.HttpRequestSignUpDto;
import com.ssg.usms.business.login.Repository.UserRepository;
import com.ssg.usms.business.login.persistence.UsmsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    public ResponseEntity<Void> SignUp(HttpRequestSignUpDto httpRequestSignUpDto){

        UsmsUser user= null;
//
        ResponseEntity res =null;

        CheckDuplicatedId(httpRequestSignUpDto.getUsername());
        CheckDuplicatePhoneNumber(httpRequestSignUpDto.getPhoneNum());
//            검증 완료 후 저장.
//            비밀번호 암호화
            String hashPwd = bCryptPasswordEncoder.encode(httpRequestSignUpDto.getPassword());
            user= UsmsUser.builder()
                    .username(httpRequestSignUpDto.getUsername())
                    .password(hashPwd)
                    .personName(httpRequestSignUpDto.getNickname())
                    .phoneNumber(httpRequestSignUpDto.getPhoneNum())
                    .email(httpRequestSignUpDto.getEmail())
                    .build();

            UsmsUser SavedUser = userRepository.save(user);

            if(SavedUser.getId()>0){
//성공시
                res = ResponseEntity.status(HttpStatus.CREATED).build();
            }

        return res;
    }

    public boolean CheckDuplicatedId(String username){

        if(userRepository.existsByUsername(username)){
            throw new AlreadyExistIdException("이미 존재하는 아이디입니다.");
        };

        return true;
    }

    public boolean CheckDuplicatePhoneNumber(String phoneNumber){

        if(userRepository.existsByPhoneNumber(phoneNumber)){
            throw new AlreadyExistPhoneNumException("이미 존재하는 전화번호입니다.");
        }

        return true;
    }

}
