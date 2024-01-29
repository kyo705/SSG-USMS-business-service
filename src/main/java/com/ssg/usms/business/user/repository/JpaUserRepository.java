package com.ssg.usms.business.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository{


    private final SpringDataJpaUserRepository springDataJpaUserRepository;
    @Override
    public void signUp(UsmsUser user){
        springDataJpaUserRepository.save(user);
    }

    @Override
    public UsmsUser findByPhoneNumber(String phoneNumber) {
        return springDataJpaUserRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public UsmsUser findByEmail(String email) {

        return springDataJpaUserRepository.findByEmail(email);
    }

    @Override
    public Optional<UsmsUser> findById(long userid) {

        return springDataJpaUserRepository.findById(userid);
    }
    @Override
    public void delete(long userid) {

        if(!springDataJpaUserRepository.existsById(userid)){
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        springDataJpaUserRepository.deleteById(userid);
    }

    @Override
    public boolean existsByEmail(String email) {

        return springDataJpaUserRepository.existsByEmail(email);
    }

    @Override
    public List<UsmsUser> findAllByPhoneNumber(String phoneNumber) {
        return springDataJpaUserRepository.findAllByPhoneNumber(phoneNumber);
    }

    @Override
    public List<UsmsUser> findAllByEmail(String email) {
        return springDataJpaUserRepository.findAllByEmail(email);
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
