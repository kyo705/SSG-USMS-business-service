package com.ssg.usms.business.user;


import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.user.dto.SecurityState;
import com.ssg.usms.business.user.dto.UserRole;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        user.setNickname("hajoo");
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
        user.setNickname("hajoo");
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
        user.setNickname("hajoo");
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
        assertThat(user).isInstanceOf(UsmsUser.class);
    }

    @DisplayName("폰넘버로 유저를 찾을때 전화번호가 없는경우 null 리턴")
    @Test
    void findByPhoneNumberNotExistUser() {
        // when
        UsmsUser user = userRepository.findByPhoneNumber("010-1233-5448");
        // then
        assertThat(user).isNull();
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

    @DisplayName("같은 이메일로 가입한 모든 사용자를 조회")
    @Test
    void findAllByEmailSuccess(){

        List< UsmsUser> dtoList = new ArrayList<>();

        dtoList.add(UsmsUser.builder()
                .id(1L)
                .username("storeOwner")
                .password("$2a$10$L/0.3f0Qm1eQDRQ4IebOD.Y0dpGQl5Xd4Q9TfkbzhJbcYVnqY77iS")
                .nickname("kyo705")
                .phoneNumber("010-1234-5678")
                .email("email@gmail.com")
                .securityState(SecurityState.BASIC)
                .isLock(false)
                .role(UserRole.ROLE_STORE_OWNER)
                .build());

        dtoList.add(UsmsUser.builder()
                .id(3L)
                .username("storeOwner2")
                .password("$2a$10$L/0.3f0Qm1eQDRQ4IebOD.Y0dpGQl5Xd4Q9TfkbzhJbcYVnqY77iS")
                .nickname("kyo705")
                .phoneNumber("010-1234-1111")
                .email("email@gmail.com")
                .securityState(SecurityState.BASIC)
                .isLock(false)
                .role(UserRole.ROLE_STORE_OWNER)
                .build());

        List<UsmsUser> list = userRepository.findAllByEmail("email@gmail.com");
        log.info(list.toString());
        assertThat(list.toString()).isEqualTo(dtoList.toString());
    }

    @DisplayName("같은 이메일로 가입한 모든 사용자를 조회실패했을때 가입된 이메일이 없는경우")
    @Test
    void findAllByEmailFailed(){


        List<UsmsUser> list = userRepository.findAllByEmail("email@gsdfmail.com");

        assertThat(list.size()).isEqualTo(0);
    }


}
