package com.ssg.usms.business.device.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDeviceRepository implements DeviceRepository{

    private final SpringJpaDataDeviceRepository springJpaDataDeviceRepository;
    @Override
    public void saveToken(UserDevice device) {
        springJpaDataDeviceRepository.save(device);
    }

    @Override
    public int deleteToken(Long userid) {
        return springJpaDataDeviceRepository.deleteByUserid(userid);
    }

}
