package com.ssg.usms.business.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UsmsUser,Integer> {
    UsmsUser findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);

    UsmsUser findByPhoneNumber(String phoneNumber);

}
