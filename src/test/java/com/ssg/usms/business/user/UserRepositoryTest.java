package com.ssg.usms.business.user;


import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.user.repository.UserRepository;
import com.ssg.usms.business.user.repository.UsmsUser;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j

@ActiveProfiles("test")
@SpringBootTest(classes = EmbeddedRedis.class)
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

        userRepository.signUp(user);
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

        userRepository.signUp(user);

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

        userRepository.signUp(user);
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

    @DisplayName("폰넘버로 유저를 찾을때 전화번호가 있는경우")
    @Test
    void findByPhoneNumberExistUser() {
        // when
        UsmsUser user = userRepository.findByPhoneNumber("010-1234-5678");
        // then
        Assertions.assertThat(user).isInstanceOf(UsmsUser.class);
    }

    @DisplayName("폰넘버로 유저를 찾을때 전화번호가 없는경우 null 리턴")
    @Test
    void findByPhoneNumberNotExistUser() {
        // when
        UsmsUser user = userRepository.findByPhoneNumber("010-1233-5448");
        // then
        Assertions.assertThat(user).isNull();
    }

    @DisplayName("이메일로 유저를 찾을때 전화번호가 있는경우")
    @Test
    void findByEmailExistUser() {
        // when
        UsmsUser user = userRepository.findByEmail("email@gmail.com");
        // then
        Assertions.assertThat(user).isInstanceOf(UsmsUser.class);
    }

    @DisplayName("이메일로 유저를 찾을때 전화번호가 있는경우")
    @Test
    void findByEmailNotExistUser() {
        // when
        UsmsUser user = userRepository.findByEmail("email@gmailasdsadf.com");
        // then
        Assertions.assertThat(user).isNull();
    }

    @DisplayName("유저를 삭제하는 경우에 유저가 존재하고 삭제가 정상적으로 완료된 경우")
    @Test
    void deleteExistUser() {
        // when
        userRepository.delete(1L);
        UsmsUser user = userRepository.findByEmail("email@gmail.com");
        // then
        Assertions.assertThat(user).isNull();
    }

    @DisplayName("유저를 삭제하는 경우에 유저가 존재하지않을때 InvalidDataAccessApiUsageException.class 리턴")
    @Test
    void deleteNotExistUser() {
        // when

        // then
        assertThrows(InvalidDataAccessApiUsageException.class,() -> userRepository.delete(5L));
    }

    @DisplayName("이메일로 사용자가 존재하는지 찾는경우 있을때 true 리턴")
    @Test
    void existEmailSuccess(){

        assertTrue(userRepository.existsByEmail("email@gmail.com"));
    }

    @DisplayName("이메일로 사용자가 존재하는지 찾는경우 없을때 false 리턴")
    @Test
    void existEmailFailed(){

        assertFalse(userRepository.existsByEmail("email@gmail.asdfasdfcom"));
    }


}
