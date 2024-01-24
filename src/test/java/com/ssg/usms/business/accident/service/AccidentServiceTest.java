package com.ssg.usms.business.accident.service;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.dto.AccidentDto;
import com.ssg.usms.business.accident.dto.AccidentStatDto;
import com.ssg.usms.business.accident.dto.HttpRequestRetrievingAccidentDto;
import com.ssg.usms.business.accident.repository.Accident;
import com.ssg.usms.business.accident.repository.AccidentRepository;
import com.ssg.usms.business.cctv.repository.Cctv;
import com.ssg.usms.business.cctv.repository.CctvRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccidentServiceTest {

    private AccidentService accidentService;

    @Mock
    private CctvRepository mockCctvRepository;

    @Mock
    private AccidentRepository mockAccidentRepository;

    @BeforeEach
    public void setup() {
        accidentService = new AccidentService(mockCctvRepository, mockAccidentRepository);
    }

    @DisplayName("[createAccident] : 이상 행동 기록 저장 테스트")
    @Test
    public void testCreateAccident() {

        //given
        String streamKey = "streamKey";
        AccidentBehavior behavior = AccidentBehavior.COME_IN;
        long timestamp = System.currentTimeMillis();

        Cctv cctv = new Cctv();
        cctv.setId(1L);
        cctv.setStreamKey(streamKey);
        cctv.setName("cctv 1");

        given(mockCctvRepository.findByStreamKey(streamKey)).willReturn(cctv);

        //when
        accidentService.createAccident(streamKey, behavior, timestamp);

        //then
        verify(mockCctvRepository, times(1)).findByStreamKey(streamKey);
        verify(mockAccidentRepository,  times(1)).save(any());

    }

    @DisplayName("[findByStoreId] : 이상행동 필터링 조건이 있을 경우 특정 매장에서 발생한 모든 이상 행동 기록 조회")
    @Test
    public void testFindByStoreId() {

        //given
        Long storeId = 1L;

        HttpRequestRetrievingAccidentDto requestParam = new HttpRequestRetrievingAccidentDto();
        requestParam.setBehavior(List.of(AccidentBehavior.COME_IN, AccidentBehavior.COME_OUT));
        requestParam.setStartDate("2024-01-13");
        requestParam.setEndDate("2024-01-23");
        requestParam.setOffset(0);
        requestParam.setSize(20);

        Accident accident1 = new Accident();
        accident1.setId(1L);
        accident1.setCctvId(1L);
        accident1.setStartTimestamp(System.currentTimeMillis());
        accident1.setBehavior(AccidentBehavior.COME_IN);

        Accident accident2 = new Accident();
        accident2.setId(2L);
        accident2.setCctvId(1L);
        accident2.setStartTimestamp(System.currentTimeMillis());
        accident2.setBehavior(AccidentBehavior.COME_IN);

        Accident accident3 = new Accident();
        accident3.setId(3L);
        accident3.setCctvId(1L);
        accident3.setStartTimestamp(System.currentTimeMillis());
        accident3.setBehavior(AccidentBehavior.COME_OUT);

        Accident accident4 = new Accident();
        accident4.setId(4L);
        accident4.setCctvId(1L);
        accident4.setStartTimestamp(System.currentTimeMillis());
        accident4.setBehavior(AccidentBehavior.COME_OUT);

        List<Accident> accidents = new ArrayList<>();
        accidents.add(accident1);
        accidents.add(accident2);
        accidents.add(accident3);
        accidents.add(accident4);

        given(mockAccidentRepository.findAllByStoreId(anyLong(), any(), anyLong(), anyLong(), anyInt(), anyInt()))
                .willReturn(accidents);

        //when
        List<AccidentDto> result = accidentService.findByStoreId(storeId, requestParam);

        //then
        verify(mockAccidentRepository, times(0)).findAllByStoreId(anyLong(), anyLong(), anyLong(), anyInt(), anyInt());
        verify(mockAccidentRepository, times(1)).findAllByStoreId(anyLong(), any(), anyLong(), anyLong(), anyInt(), anyInt());
        assertThat(result.size()).isEqualTo(accidents.size());


    }

    @DisplayName("[findByStoreId] : 이상행동 필터링 조건이 없을 경우 특정 매장에서 발생한 모든 이상 행동 기록 조회")
    @Test
    public void testFindByStoreIdWithoutBehavior() {

        //given
        Long storeId = 1L;

        HttpRequestRetrievingAccidentDto requestParam = new HttpRequestRetrievingAccidentDto();
        requestParam.setStartDate("2024-01-13");
        requestParam.setEndDate("2024-01-23");
        requestParam.setOffset(0);
        requestParam.setSize(20);

        Accident accident1 = new Accident();
        accident1.setId(1L);
        accident1.setCctvId(1L);
        accident1.setStartTimestamp(System.currentTimeMillis());
        accident1.setBehavior(AccidentBehavior.COME_IN);

        Accident accident2 = new Accident();
        accident2.setId(2L);
        accident2.setCctvId(1L);
        accident2.setStartTimestamp(System.currentTimeMillis());
        accident2.setBehavior(AccidentBehavior.COME_IN);

        Accident accident3 = new Accident();
        accident3.setId(3L);
        accident3.setCctvId(1L);
        accident3.setStartTimestamp(System.currentTimeMillis());
        accident3.setBehavior(AccidentBehavior.COME_OUT);

        Accident accident4 = new Accident();
        accident4.setId(4L);
        accident4.setCctvId(1L);
        accident4.setStartTimestamp(System.currentTimeMillis());
        accident4.setBehavior(AccidentBehavior.COME_OUT);

        List<Accident> accidents = new ArrayList<>();
        accidents.add(accident1);
        accidents.add(accident2);
        accidents.add(accident3);
        accidents.add(accident4);

        given(mockAccidentRepository.findAllByStoreId(anyLong(), anyLong(), anyLong(), anyInt(), anyInt()))
                .willReturn(accidents);

        //when
        List<AccidentDto> result = accidentService.findByStoreId(storeId, requestParam);

        //then
        verify(mockAccidentRepository, times(1)).findAllByStoreId(anyLong(), anyLong(), anyLong(), anyInt(), anyInt());
        verify(mockAccidentRepository, times(0)).findAllByStoreId(anyLong(), any(), anyLong(), anyLong(), anyInt(), anyInt());
        assertThat(result.size()).isEqualTo(accidents.size());


    }

    @DisplayName("[findAccidentStatsByStoreId] : 특정 매장에서 발생한 이상 행동 통계 기록 조회")
    @Test
    public void testFindAccidentStatsByStoreId() {

        //given
        Long storeId = 1L;
        String startDate = "2024-01-13";
        String endDate = "2024-01-23";

        AccidentStatDto accident1 = new AccidentStatDto();
        accident1.setStoreId(storeId);
        accident1.setBehavior(AccidentBehavior.COME_IN);
        accident1.setStartDate(startDate);
        accident1.setEndDate(endDate);
        accident1.setCount(20L);

        AccidentStatDto accident2 = new AccidentStatDto();
        accident2.setStoreId(storeId);
        accident2.setBehavior(AccidentBehavior.COME_OUT);
        accident2.setStartDate(startDate);
        accident2.setEndDate(endDate);
        accident2.setCount(20L);

        AccidentStatDto accident3 = new AccidentStatDto();
        accident3.setStoreId(storeId);
        accident3.setBehavior(AccidentBehavior.DESTROY);
        accident3.setStartDate(startDate);
        accident3.setEndDate(endDate);
        accident3.setCount(20L);

        List<AccidentStatDto> accidents = new ArrayList<>();
        accidents.add(accident2);
        accidents.add(accident2);
        accidents.add(accident3);

        given(mockAccidentRepository.findAccidentStats(anyLong(), anyLong(), anyLong()))
                .willReturn(accidents);

        //when
        List<AccidentStatDto> result = accidentService.findAccidentStatsByStoreId(storeId, startDate, endDate);

        //then
        assertThat(result.size()).isEqualTo(accidents.size());


    }
}
