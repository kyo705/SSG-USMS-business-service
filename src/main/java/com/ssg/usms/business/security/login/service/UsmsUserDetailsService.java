package com.ssg.usms.business.security.login.service;


import com.ssg.usms.business.security.login.UsmsUserDetails;
import com.ssg.usms.business.user.repository.UserRepository;
import com.ssg.usms.business.user.repository.UsmsUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UsmsUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username){

        UsmsUser user = userRepository.findByUsername(username);

        if(user == null) return null;

        return new UsmsUserDetails(user);
    }
}
