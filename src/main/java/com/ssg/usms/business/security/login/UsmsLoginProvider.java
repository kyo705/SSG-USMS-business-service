package com.ssg.usms.business.security.login;

import com.ssg.usms.business.security.login.service.UsmsUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import static com.ssg.usms.business.security.login.constant.LoginConstant.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class UsmsLoginProvider implements AuthenticationProvider {

    private final UsmsUserDetailsService usmsUserDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserDetails userDetails = usmsUserDetailsService.loadUserByUsername(username);

        if(userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())){

            throw new BadCredentialsException(NOT_MATCHED_USERNAME_PASSWORD_LOGIN);
        }
        if(!userDetails.isAccountNonExpired() || !userDetails.isAccountNonExpired()){

            throw new AccountExpiredException(ACCOUNT_EXPIRED);
        }
        if(!userDetails.isEnabled()){

            throw new DisabledException(DISABLED_ACCOUNT);
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
