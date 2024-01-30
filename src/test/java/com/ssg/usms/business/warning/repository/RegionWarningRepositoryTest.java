package com.ssg.usms.business.warning.repository;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.config.EmbeddedRedis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = EmbeddedRedis.class)
public class RegionWarningRepositoryTest {

    @Autowired
    private RegionWarningRepository regionWarningRepository;

    @DisplayName("[save] : 지역 경고 알림 저장 테스트")
    @Test
    public void testSave() {

        //given
        String region = "서울특별시 강남구";
        String date = "2024-01-22";
        int count = 20;

        RegionWarning regionWarning = new RegionWarning();
        regionWarning.setRegion(region);
        regionWarning.setBehavior(AccidentBehavior.FIGHT);
        regionWarning.setCount(count);
        regionWarning.setDate(Date.valueOf(date));

        assertThat(regionWarning.getId()).isNull();

        //when
        regionWarningRepository.save(regionWarning);

        //then
        assertThat(regionWarning.getId()).isNotNull();
    }

    @DisplayName("[findByRegion] : 지역별 알림 조회 요청 테스트")
    @Test
    public void testFindByRegion() {

        //given
        String region = "서울특별시 강남구";
        String startDate = "2024-01-22";
        String endDate = "2024-01-24";

        //when
        List<RegionWarning> result = regionWarningRepository.findByRegion(region, startDate, endDate, 0, 20);

        for(RegionWarning regionWarning : result) {
            assertThat(regionWarning.getDate()).isAfterOrEqualTo(Date.valueOf(startDate));
            assertThat(regionWarning.getDate()).isBeforeOrEqualTo(Date.valueOf(endDate));
        }
    }

}
