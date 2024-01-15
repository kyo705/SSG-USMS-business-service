package com.ssg.usms.business.User.Repository;

import com.ssg.usms.business.User.persistence.UsmsUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UsmsUser,Integer> {
    UsmsUser findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);

}