package com.ssg.usms.business.accident.service;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.dto.AccidentDto;
import com.ssg.usms.business.accident.dto.AccidentStatDto;
import com.ssg.usms.business.accident.dto.HttpRequestRetrievingAccidentDto;
import com.ssg.usms.business.accident.repository.Accident;
import com.ssg.usms.business.accident.repository.AccidentRepository;
import com.ssg.usms.business.cctv.repository.Cctv;
import com.ssg.usms.business.cctv.repository.CctvRepository;
import com.ssg.usms.business.device.repository.DeviceRepository;
import com.ssg.usms.business.device.repository.UsmsDevice;
import com.ssg.usms.business.notification.service.NotificationService;
import com.ssg.usms.business.store.repository.Store;
import com.ssg.usms.business.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import static com.ssg.usms.business.accident.constant.AccidentConstants.ACCIDENT_OCCURRENCE_MESSAGE;
import static com.ssg.usms.business.accident.constant.AccidentConstants.ACCIDENT_OCCURRENCE_SUBJECT;

@Service
@RequiredArgsConstructor
public class AccidentService {

    private final NotificationService firebaseNotificationService;
    private final DeviceRepository deviceRepository;
    private final StoreRepository storeRepository;
    private final CctvRepository cctvRepository;
    private final AccidentRepository accidentRepository;

    @Transactional
    public void createAccident(String streamKey, AccidentBehavior behavior, Long timestamp) {

        Cctv cctv = cctvRepository.findByStreamKey(streamKey);
        Long cctvId = cctv.getId();

        Store store = storeRepository.findById(cctv.getStoreId());
        Long userId = store.getUserId();

        List<UsmsDevice> devices = deviceRepository.findByUserId(userId);
        devices.forEach(device -> {
            try {
                firebaseNotificationService.send(
                        device.getToken(),
                        ACCIDENT_OCCURRENCE_SUBJECT,
                        String.format(ACCIDENT_OCCURRENCE_MESSAGE, store.getStoreName(), behavior.name())
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Accident accident = Accident.init(cctvId, behavior, timestamp);
        accidentRepository.save(accident);
    }

    @Transactional(readOnly = true)
    public List<AccidentDto> findByStoreId(Long storeId, HttpRequestRetrievingAccidentDto requestParam) {

        long startTimestamp = requestParam.getStartDate() == null ? 0L : parseStartTimestamp(requestParam.getStartDate());
        long endTimestamp = requestParam.getEndDate() == null ? System.currentTimeMillis() : parseEndTimestamp(requestParam.getEndDate());

        // 이상 행동 조건이 없을 때
        if(requestParam.getBehavior() == null || requestParam.getBehavior().isEmpty()) {
            return accidentRepository.findAllByStoreId(storeId,
                                                    startTimestamp,
                                                    endTimestamp,
                                                    requestParam.getAccidentId(),
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
                                                requestParam.getAccidentId(),
                                                requestParam.getSize()
                )
                .stream()
                .map(AccidentDto::new)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<AccidentStatDto> findAccidentStatsByStoreId(Long storeId, String startDate, String endDate) {

        long startTimestamp = startDate == null ? 0L : parseStartTimestamp(startDate);
        long endTimestamp = endDate == null ? System.currentTimeMillis() : parseEndTimestamp(endDate);

        return accidentRepository.findAccidentStats(storeId, startTimestamp, endTimestamp);
    }

    private long parseStartTimestamp(String startDate) {

        return LocalDate.parse(startDate).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    private long parseEndTimestamp(String endDate) {

        return LocalDate.parse(endDate).plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
