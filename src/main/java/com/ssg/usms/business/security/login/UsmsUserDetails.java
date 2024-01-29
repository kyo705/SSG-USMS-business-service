package com.ssg.usms.business.security.login;

import com.ssg.usms.business.user.dto.SecurityState;
import com.ssg.usms.business.user.repository.UsmsUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Slf4j
@AllArgsConstructor
@NoArgsConstructor

public class UsmsUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String personName;
    private String phoneNumber;
    private String email;
    private SecurityState securityState;
    private boolean isLock;
    private List<UsmsGrantedAuthority> authorities;

    public UsmsUserDetails(UsmsUser user){

        this.id = user.getId();
        this.username=user.getUsername();
        this.password=user.getPassword();
        this.personName=user.getNickname();
        this.phoneNumber=user.getPhoneNumber();
        this.email=user.getEmail();
        this.securityState=user.getSecurityState();
        this.isLock=false;

        authorities = new ArrayList<>();
        authorities.add(new UsmsGrantedAuthority(user.getRole().name()));
//        this.authorities=user.getRole();


    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }



    @Getter
    @NoArgsConstructor
    static class UsmsGrantedAuthority implements GrantedAuthority{

        private String authority;

        public UsmsGrantedAuthority(String authority){

            this.authority = authority;
        }

        @Override
        public String getAuthority(){

            return authority;
        }

    }
}
