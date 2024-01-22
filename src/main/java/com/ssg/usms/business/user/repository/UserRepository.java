package com.ssg.usms.business.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository  {
    UsmsUser findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNum);
    void signUp(UsmsUser user);
}
