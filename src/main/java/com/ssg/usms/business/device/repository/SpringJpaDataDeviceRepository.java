package com.ssg.usms.business.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringJpaDataDeviceRepository extends JpaRepository<UsmsDevice,Long> {

    List<UsmsDevice> findByUserId(Long userId);

    int deleteByUserId(Long userid);

}
