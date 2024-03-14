package com.ssg.usms.business.store.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SpringDataJpaStoreRepository extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {

    List<Store> findByUserIdAndIdGreaterThan(Long userId, Long storeId, Pageable Pageable);
    List<Store> findByStoreAddressLike(String region);

}
