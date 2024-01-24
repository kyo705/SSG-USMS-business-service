package com.ssg.usms.business.accident.service;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.dto.AccidentDto;
import com.ssg.usms.business.accident.dto.AccidentStatDto;
import com.ssg.usms.business.accident.dto.HttpRequestRetrievingAccidentDto;
import com.ssg.usms.business.accident.repository.Accident;
import com.ssg.usms.business.accident.repository.AccidentRepository;
import com.ssg.usms.business.cctv.repository.CctvRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccidentService {

    private final CctvRepository cctvRepository;
    private final AccidentRepository accidentRepository;

    @Transactional
    public void createAccident(String streamKey, AccidentBehavior behavior, Long timestamp) {

        Long cctvId = cctvRepository.findByStreamKey(streamKey).getId();

        Accident accident = Accident.init(cctvId, behavior, timestamp);
        accidentRepository.save(accident);
    }

    @Transactional(readOnly = true)
    public List<AccidentDto> findByStoreId(Long storeId, HttpRequestRetrievingAccidentDto requestParam) {

        long startTimestamp = parseTimestamp(requestParam.getStartDate());
        long endTimestamp = parseTimestamp(requestParam.getEndDate());

        // 이상 행동 조건이 없을 때
        if(requestParam.getBehavior() == null || requestParam.getBehavior().isEmpty()) {
            return accidentRepository.findAllByStoreId(storeId,
                                                    startTimestamp,
                                                    endTimestamp,
                                                    requestParam.getOffset(),
                                                    requestParam.getSize()
                    )
                    .stream()
                    .map(AccidentDto::new)
                    .collect(Collectors.toList());
        }

        // 이상 행동에 따른 필터링 있을 때
        return accidentRepository.findAllByStoreId(storeId,
                                                requestParam.getBehavior(),
                                                startTimestamp,
                                                endTimestamp,
                                                requestParam.getOffset(),
                                                requestParam.getSize()
                )
                .stream()
                .map(AccidentDto::new)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<AccidentStatDto> findAccidentStatsByStoreId(Long storeId, String startDate, String endDate) {

        long startTimestamp = parseTimestamp(startDate);
        long endTimestamp = parseTimestamp(endDate);

        return accidentRepository.findAccidentStats(storeId, startTimestamp, endTimestamp);
    }

    private long parseTimestamp(String startDate) {

        return LocalDate.parse(startDate).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
