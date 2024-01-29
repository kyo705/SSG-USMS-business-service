package com.ssg.usms.business.warning;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.warning.repository.RegionWarning;

import java.sql.Date;
import java.util.List;

public class RegionWarningTestSetup {

    public static List<RegionWarning> getRegionWarning(String region) {

        RegionWarning regionWarning1 = new RegionWarning();
        regionWarning1.setId(1L);
        regionWarning1.setBehavior(AccidentBehavior.FIGHT);
        regionWarning1.setRegion(region);
        regionWarning1.setDate(Date.valueOf("2024-01-24"));
        regionWarning1.setCount(10);

        RegionWarning regionWarning2 = new RegionWarning();
        regionWarning2.setId(2L);
        regionWarning2.setBehavior(AccidentBehavior.STEAL);
        regionWarning2.setRegion(region);
        regionWarning2.setDate(Date.valueOf("2024-01-24"));
        regionWarning2.setCount(20);

        RegionWarning regionWarning3 = new RegionWarning();
        regionWarning3.setId(3L);
        regionWarning3.setBehavior(AccidentBehavior.DRUNKEN);
        regionWarning3.setRegion(region);
        regionWarning3.setDate(Date.valueOf("2024-01-24"));
        regionWarning3.setCount(30);

        return List.of(regionWarning1, regionWarning2, regionWarning3);
    }
}
