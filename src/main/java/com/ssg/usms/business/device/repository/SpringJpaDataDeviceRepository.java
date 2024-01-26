package com.ssg.usms.business.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringJpaDataDeviceRepository extends JpaRepository<UserDevice,Long> {

    int deleteByUserid(Long userid);
}
