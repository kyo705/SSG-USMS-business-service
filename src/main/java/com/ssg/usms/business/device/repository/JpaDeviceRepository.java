package com.ssg.usms.business.device.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaDeviceRepository implements DeviceRepository{

    private final SpringJpaDataDeviceRepository springJpaDataDeviceRepository;

    @Override
    public List<UsmsDevice> findByUserId(Long userId) {
        return springJpaDataDeviceRepository.findByUserId(userId);
    }

    @Override
    public void saveToken(UsmsDevice device) {
        springJpaDataDeviceRepository.save(device);
    }

    @Override
    public boolean existsByToken(String token) {
        return springJpaDataDeviceRepository.existsByToken(token);
    }

    @Override
    public int deleteToken(Long userId) {
        return springJpaDataDeviceRepository.deleteByUserId(userId);
    }

}
