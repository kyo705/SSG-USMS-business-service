package com.ssg.usms.business.security;


import com.ssg.usms.business.login.Repository.UserRepository;

import com.ssg.usms.business.login.persistence.UsmsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CusUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UsmsUser userEntity = repository.findByUsername(username);
        return null;
    }
}
