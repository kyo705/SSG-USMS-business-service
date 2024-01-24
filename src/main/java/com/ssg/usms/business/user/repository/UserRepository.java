package com.ssg.usms.business.user.repository;

import com.ssg.usms.business.user.dto.HttpRequestModifyUserDto;

import java.util.Optional;


public interface UserRepository  {
    UsmsUser findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNum);
    void signUp(UsmsUser user);

    UsmsUser findByPhoneNumber(String phoneNumber);

    UsmsUser findByEmail(String email);

    Optional<UsmsUser> findById(long userid);
    void delete(String principal);
}
