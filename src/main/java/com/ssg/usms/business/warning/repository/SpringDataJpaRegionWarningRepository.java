package com.ssg.usms.business.warning.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface SpringDataJpaRegionWarningRepository extends JpaRepository<RegionWarning, Long> {


    List<RegionWarning> findByRegionAndDateBetweenAndIdGreaterThan(String region, Date startDate, Date endDate, Long regionWarningId, Pageable pageable);

    List<RegionWarning> findByDateBetweenAndIdGreaterThan(Date startDate, Date endDate, Long regionWarningId, PageRequest of);
}
