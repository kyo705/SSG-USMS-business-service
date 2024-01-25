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
    public List<RegionWarning> findByRegion(String region, String startDate, String endDate, int offset, int size) {

        return springDataJpaRepository.findByRegionAndDateBetween(region,
                Date.valueOf(startDate),
                Date.valueOf(endDate),
                PageRequest.of(offset, size));
    }
}
