package com.ssg.usms.business.accident.repository;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.dto.AccidentRegionDto;
import com.ssg.usms.business.accident.dto.AccidentStatDto;
import com.ssg.usms.business.config.EmbeddedRedis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(classes = EmbeddedRedis.class)
public class AccidentRepositoryTest {

    @Autowired
    private AccidentRepository accidentRepository;

    @DisplayName("[save] : 이상행동 유형 저장 테스트")
    @Test
    public void testSave() {

        //given
        Accident accident = new Accident();
        accident.setCctvId(1L);
        accident.setBehavior(AccidentBehavior.COME_IN);
        accident.setStartTimestamp(System.currentTimeMillis());

        assertThat(accident.getId()).isNull();

        //when
        accidentRepository.save(accident);

        //then
        assertThat(accident.getId()).isNotNull();

    }

    @DisplayName("[findAllByCctvId] : cctv id에 해당하는 이상행동 기록 조회")
    @Test
    public void testFindByCctvId() {

        //given
        Long cctvId = 1L;
        int offset = 0;
        int size = 10;

        //when
        List<Accident> result = accidentRepository.findAllByCctvId(cctvId, offset, size);

        assertThat(result.size()).isLessThanOrEqualTo(size);
        for(Accident accident : result) {
            assertThat(accident.getCctvId()).isEqualTo(cctvId);
        }

    }

    @DisplayName("[findAccidentStats] : 특정 시간대의 매장 내 이상 행동 기록 통계 조회")
    @Test
    public void testFindAccidentStatsDto() {

        //given
        Long storeId = 1L;
        long startTimestamp = 1000000000L;
        long endTimestamp = System.currentTimeMillis();

        //when
        List<AccidentStatDto> result = accidentRepository.findAccidentStats(storeId, startTimestamp, endTimestamp);

        for(AccidentStatDto accidentStat : result) {
            assertThat(accidentStat.getStoreId()).isEqualTo(storeId);
            assertThat(accidentStat.getStartDate())
                    .isEqualTo(new Timestamp(startTimestamp)
                            .toLocalDateTime()
                            .toLocalDate()
                            .format(DateTimeFormatter.ofPattern("yy-MM-dd"))
                    );

            assertThat(accidentStat.getEndDate())
                    .isEqualTo(new Timestamp(endTimestamp)
                            .toLocalDateTime()
                            .toLocalDate()
                            .format(DateTimeFormatter.ofPattern("yy-MM-dd"))
                    );
        }

    }

    @DisplayName("[findAllByStoreId] : 특정 매장에 해당하는 모든 이상 행동 기록 조회")
    @Test
    public void testFindAllByStoreId() {

        //given
        Long storeId = 1L;
        long startTimestamp = 1000000000L;
        long endTimestamp = System.currentTimeMillis();
        long offset = 0L;
        int size = 20;
        List<AccidentBehavior> behaviors = new ArrayList<>();
        behaviors.add(AccidentBehavior.FIGHT);
        behaviors.add(AccidentBehavior.STEAL);

        //when
        List<Accident> result = accidentRepository.findAllByStoreId(storeId, behaviors, startTimestamp, endTimestamp, offset, size);
        assertThat(result.size()).isLessThanOrEqualTo(size);
        for(Accident accident : result) {
            assertThat(accident.getBehavior()).isIn(AccidentBehavior.FIGHT, AccidentBehavior.STEAL);
        }

    }

    @DisplayName("[findAllByStoreId] : 특정 매장에 해당하는 모든 이상 행동 기록 조회")
    @Test
    public void testFindAllByStoreIdWithoutBehavior() {

        //given
        Long storeId = 1L;
        long startTimestamp = 1000000000L;
        long endTimestamp = System.currentTimeMillis();
        long offset = 0L;
        int size = 20;

        //when
        List<Accident> result = accidentRepository.findAllByStoreId(storeId, startTimestamp, endTimestamp, offset, size);
        assertThat(result.size()).isLessThanOrEqualTo(size);

    }

    @DisplayName("[findAccidentRegion] : 특정 시간동안 발생한 이상행동과 해당 지역을 조회")
    @Test
    public void testFindAccidentRegion() {

        //given
        long offset = 0L;
        int size = 20;
        long startTimestamp = 0;
        long endTimestamp = System.currentTimeMillis();

        //when
        List<AccidentRegionDto> accidentRegionDtoList = accidentRepository.findAccidentRegion(startTimestamp, endTimestamp, offset, size);

        //then
        System.out.println(accidentRegionDtoList);
    }
}
