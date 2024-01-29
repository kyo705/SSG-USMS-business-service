package com.ssg.usms.business.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataJpaUserRepository extends JpaRepository<UsmsUser,Long> {

    List<UsmsUser> findAllByEmail(String Email);
    List<UsmsUser> findAllByPhoneNumber(String phoneNumber);

    UsmsUser findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);


    boolean existsByEmail(String username);

    UsmsUser findByPhoneNumber(String phoneNumber);

    UsmsUser findByEmail(String email);

}
