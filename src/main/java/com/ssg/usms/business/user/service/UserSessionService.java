package com.ssg.usms.business.user.service;

import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import com.ssg.usms.business.user.repository.UserRepository;
import com.ssg.usms.business.user.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ssg.usms.business.user.constant.UserConstants.NOT_ALLOWED_SESSION_LITERAL;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;

    private final UserRepository userRepository;

    private UsmsUserDetails getPrincipalFromCurrentSession() throws IllegalAccessException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsmsUserDetails userDetails = (UsmsUserDetails) authentication.getPrincipal();
            if(userDetails == null) {
                throw new IllegalAccessException(NOT_ALLOWED_SESSION_LITERAL);
            }
            return userDetails;
        } catch (NullPointerException | ClassCastException e) {
            throw new IllegalAccessException(NOT_ALLOWED_SESSION_LITERAL);
        }

    }

    public HttpResponseUserDto findUserBySession() throws IllegalAccessException {

        UsmsUserDetails userDetails = getPrincipalFromCurrentSession();

        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .personName(userDetails.getPersonName())
                .phoneNumber(userDetails.getPhoneNumber())
                .securityState(userDetails.getSecurityState())
                .email(userDetails.getEmail())
                .build();

        return dto;
    }

    public void deleteUser(long userId) throws IllegalAccessException {

        UsmsUserDetails principal = getPrincipalFromCurrentSession();

        if (principal.getId() != userId){
            throw new IllegalAccessException(NOT_ALLOWED_SESSION_LITERAL);
        }

        userSessionRepository.deleteSession(principal.getUsername());
        userRepository.delete(principal.getUsername());
    }


}
