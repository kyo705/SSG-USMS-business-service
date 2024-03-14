package com.ssg.usms.business.warning.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaRegionWarningRepository implements RegionWarningRepository {

    private final SpringDataJpaRegionWarningRepository springDataJpaRepository;
    @Override
    public void save(RegionWarning regionWarning) {

        springDataJpaRepository.save(regionWarning);
    }

    @Override
    public List<RegionWarning> findByRegion(String region, String startDate, String endDate, long regionWarningId, int size) {

        return springDataJpaRepository.findByRegionAndDateBetweenAndIdGreaterThan(region,
                Date.valueOf(startDate),
                Date.valueOf(endDate),
                regionWarningId,
                PageRequest.of(0, size));
    }

    @Override
    public List<RegionWarning> findAll(String startDate, String endDate, long regionWarningId, int size) {

        return springDataJpaRepository.findByDateBetweenAndIdGreaterThan(
                Date.valueOf(startDate),
                Date.valueOf(endDate),
                regionWarningId,
                PageRequest.of(0, size));
    }
}
