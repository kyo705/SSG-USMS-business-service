package com.ssg.usms.business.cctv.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataJpaCctvRepository extends JpaRepository<Cctv, Long> {


    List<Cctv> findByStoreId(Long storeId, Pageable Pageable);

    Optional<Cctv> findByStreamKey(String streamKey);

}
