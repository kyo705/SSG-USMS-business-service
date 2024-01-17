package com.ssg.usms.business.store.repository;

import com.ssg.usms.business.store.constant.StoreState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Store findByBusinessLicenseCodeAndStoreState(String businessLicenseCode, StoreState state);
}
