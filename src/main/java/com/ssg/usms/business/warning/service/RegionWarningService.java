package com.ssg.usms.business.warning.service;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.dto.AccidentRegionDto;
import com.ssg.usms.business.accident.repository.AccidentRepository;
import com.ssg.usms.business.device.repository.DeviceRepository;
import com.ssg.usms.business.device.repository.UsmsDevice;
import com.ssg.usms.business.notification.service.NotificationService;
import com.ssg.usms.business.store.repository.Store;
import com.ssg.usms.business.store.repository.StoreRepository;
import com.ssg.usms.business.warning.dto.RegionWarningDto;
import com.ssg.usms.business.warning.repository.RegionWarning;
import com.ssg.usms.business.warning.repository.RegionWarningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ssg.usms.business.warning.RegionWarningConstant.*;

@Service
@RequiredArgsConstructor
public class RegionWarningService {

    private final NotificationService firebaseNotificationService;
    private final DeviceRepository deviceRepository;
    private final StoreRepository storeRepository;
    private final AccidentRepository accidentRepository;
    private final RegionWarningRepository regionWarningRepository;

    @Transactional(readOnly = true)
    public List<RegionWarningDto> findByRegion(Long storeId, String startDate, String endDate, int offset, int size) {

        Store store = storeRepository.findById(storeId);
        String[] address = store.getStoreAddress().split(" "); // 서울특별시 강남구 강남대로42길

        String region = address[0] + " " + address[1];
        if(startDate == null) {
            startDate = "2000-01-01";
        }
        if(endDate == null) {
            endDate = LocalDate.now().toString();
        }

        return regionWarningRepository.findByRegion(region, startDate, endDate, offset, size)
                .stream()
                .map(RegionWarningDto::new)
                .collect(Collectors.toList());
    }


    @Transactional
    @Scheduled(cron = "${usms.schedule.createRegionWarning.cron}", zone = "${usms.schedule.timeZone}")
    public void createRegionWarning() {

        long endTimestamp = System.currentTimeMillis();
        long startTimestamp = endTimestamp - 1000*60*60*24;

        int offset = 0;
        int size = 100;

        Map<String, Long> regionBehaviorMap = new HashMap<>();

        while(true) {
            List<AccidentRegionDto> accidentRegionDtos = accidentRepository.findAccidentRegion(startTimestamp, endTimestamp, offset, size);
            for(AccidentRegionDto accidentRegion : accidentRegionDtos) {
                String key = accidentRegion.getBehavior().getCode() + " "
                        + accidentRegion.getStoreAddress().split(" ")[0] + " "
                        + accidentRegion.getStoreAddress().split(" ")[1];

                regionBehaviorMap.put(key, regionBehaviorMap.getOrDefault(key, 0L) + 1);
            }

            if(accidentRegionDtos.size() != size) {
                break;
            }
            offset += size;
        }

        regionBehaviorMap.forEach((key, value) -> {

            if(value > MIN_REGION_WARNING_COUNT) {
                String[] keyChunk = key.split(" ");

                RegionWarning regionWarning = new RegionWarning();
                regionWarning.setBehavior(AccidentBehavior.valueOfCode(Integer.parseInt(keyChunk[0])));
                regionWarning.setRegion(keyChunk[1] + " " + keyChunk[2]);
                regionWarning.setCount(value);
                regionWarning.setDate(Date.valueOf(LocalDate.now().minusDays(1)));

                regionWarningRepository.save(regionWarning);
            }
        });
    }

    @Scheduled(cron = "${usms.schedule.sendRegionWarningNotification.cron}", zone = "${usms.schedule.timeZone}")
    public void sendRegionWarningNotification() throws IOException {

        int offset = 0;
        int size = 100;

        while (true) {
            List<RegionWarning> regionWarnings = regionWarningRepository
                    .findAll(LocalDate.now().minusDays(1).toString(), LocalDate.now().toString(), offset, size);

            for(RegionWarning regionWarning : regionWarnings) {
                //지역을 통해 매장 찾아냄
                String region = regionWarning.getRegion();
                String regionLikeKeyword = region + "%";
                List<Store> stores = storeRepository.findByRegion(regionLikeKeyword);

                //매장을 소유한 유저에게 알림을 전송
                for(Store store : stores) {
                    Long userId = store.getUserId();
                    List<UsmsDevice> devices = deviceRepository.findByUserId(userId);

                    for(UsmsDevice device : devices) {
                        firebaseNotificationService.send(
                                device.getToken(),
                                REGION_WARNING_SUBJECT,
                                String.format(REGION_WARNING_MESSAGE, region, regionWarning.getBehavior(), regionWarning.getCount())
                        );
                    }
                }
            }

            if(regionWarnings.size() != size) {
                break;
            }
            offset++;
        }

    }
}
