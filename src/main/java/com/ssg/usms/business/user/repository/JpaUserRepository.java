package com.ssg.usms.business.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository{

    private final SpringDataJpaUserRepository springDataJpaUserRepository;
    @Override
    public void signUp(UsmsUser user){
        springDataJpaUserRepository.save(user);
    }
    @Override
    public UsmsUser findByUsername(String username) {
        return springDataJpaUserRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return springDataJpaUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return springDataJpaUserRepository.existsByPhoneNumber(phoneNumber);
    }

}
