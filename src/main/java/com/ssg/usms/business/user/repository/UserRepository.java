package com.ssg.usms.business.user.repository;


import java.util.List;
import java.util.Optional;


public interface UserRepository  {
    UsmsUser findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByPhoneNumber(String phoneNum);
    void signUp(UsmsUser user);
    UsmsUser findByPhoneNumber(String phoneNumber);
    Optional<UsmsUser> findById(long userid);
    void delete(long userid);

    boolean existsByEmail(String email);

    List<UsmsUser> findAllByPhoneNumber(String phoneNumber);
    List<UsmsUser> findAllByEmail(String email);
}
