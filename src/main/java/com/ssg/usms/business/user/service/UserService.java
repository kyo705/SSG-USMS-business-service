package com.ssg.usms.business.user.service;

import com.ssg.usms.business.user.dto.HttpRequestModifyUserDto;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import com.ssg.usms.business.user.exception.NotExistingUserException;
import com.ssg.usms.business.user.repository.UserRepository;
import com.ssg.usms.business.user.repository.UsmsUser;
import com.ssg.usms.business.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {


    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

//   유저 데이터 조회
    public HttpResponseUserDto findUserByValue(String token){

        int code = Integer.parseInt((String) jwtUtil.getClaim(token).get("code"));
        String value = (String) jwtUtil.getClaim(token).get("value");
        UsmsUser user = null;

        if (code==0){
            user = userRepository.findByEmail(value);
        }
        if(code == 1){
            user = userRepository.findByPhoneNumber(value);
        }
        if( user ==null){
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
    public UsmsUser ModifyUser(long value, HttpRequestModifyUserDto httpRequestModifyUserDto){

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

}
