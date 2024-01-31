package com.ssg.usms.business.warning.repository;

import java.util.List;

public interface RegionWarningRepository {

    void save(RegionWarning regionWarning);

    List<RegionWarning> findByRegion(String region, String startDate, String endDate, int offset, int size);

    List<RegionWarning> findAll(String startDate, String endDate, int offset, int size);

}
