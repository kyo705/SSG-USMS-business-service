package com.ssg.usms.business.SignUp;


import com.ssg.usms.business.User.Repository.UserRepository;
import com.ssg.usms.business.User.persistence.UsmsUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("유저가 존재할때 유저를 리턴하는지 테스트.")
    @Transactional
    @Test
    void findByUsernameWhenUserExistsShouldReturnUser() {
        // given
        UsmsUser user = new UsmsUser();
        user.setUsername("testUser");
        user.setEmail("ans10123@asdf.com");
        user.setPassword("hasedhashed123@12");
        user.setPersonName("hajoo");
        user.setPhoneNumber("010-1234-2323");

        userRepository.save(user);
        // when
        UsmsUser foundUser = userRepository.findByUsername("testUser");

        // then
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }

    @DisplayName("존재하지않는 유저를 찾았을때 Null을 리턴")
    @Test
    void findByUsernameWhenUserDoesNotExistShouldReturnNull() {
        // when
        UsmsUser foundUser = userRepository.findByUsername("nonExistingUser");
        // then
        assertNull(foundUser);
    }

    @Transactional
    @DisplayName("존재하는 유저를 찾았을때 existByUsername에서 true를 리턴한다.")
    @Test
    void existsByUsernameWhenUserExistsShouldReturnTrue() {
        // given
        UsmsUser user = new UsmsUser();
        user.setUsername("testUser");
        user.setEmail("ans10123@asdf.com");
        user.setPassword("hasedhashed123@12");
        user.setPersonName("hajoo");
        user.setPhoneNumber("010-1234-2323");

        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByUsername("testUser");
        // then
        assertTrue(exists);
    }


    @DisplayName("유저가 없을때 existsByUsername에서 False를 리턴")
    @Test
    void existsByUsernameWhenUserDoesNotExistShouldReturnFalse() {
        // when
        boolean exists = userRepository.existsByUsername("nonExistingUser");
        // then
        assertFalse(exists);
    }

    @DisplayName("존재하는 Number을 찾았을때 existsByPhoneNumber 에서 True를 리턴")
    @Transactional
    @Test
    void existsByPhoneNumberWhenUserExistsShouldReturnTrue() {
        // given
        UsmsUser user = new UsmsUser();
        user.setUsername("testUser");
        user.setEmail("ans10123@asdf.com");
        user.setPassword("hasedhashed123@12");
        user.setPersonName("hajoo");
        user.setPhoneNumber("010-1234-2323");

        userRepository.save(user);
        // when
        boolean exists = userRepository.existsByPhoneNumber("010-1234-2323");

        // then
        assertTrue(exists);
    }

    @DisplayName("존재하는 Number을 찾았을때 existsByPhoneNumber 에서 False를 리턴")
    @Test
    void existsByPhoneNumberWhenUserDoesNotExistShouldReturnFalse() {
        // when
        boolean exists = userRepository.existsByPhoneNumber("010-9876-5432");
        // then
        assertFalse(exists);
    }
}