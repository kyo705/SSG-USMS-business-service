package com.ssg.usms.business.login.Repository;

import com.ssg.usms.business.login.persistence.UsmsUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UsmsUser,Integer> {
    UsmsUser findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);

}
