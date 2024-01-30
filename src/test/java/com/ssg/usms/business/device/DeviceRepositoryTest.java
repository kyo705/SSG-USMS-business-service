package com.ssg.usms.business.device;

import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.device.repository.DeviceRepository;
import com.ssg.usms.business.device.repository.SpringJpaDataDeviceRepository;
import com.ssg.usms.business.device.repository.UsmsDevice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(classes = EmbeddedRedis.class)
public class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SpringJpaDataDeviceRepository jpaDataDeviceRepository;
    @DisplayName("삭제할 값이 있다면 삭제 후 0 이 아닌 값을 리턴")
    @Test
    public void TestDeleteDevice(){
        UsmsDevice device = UsmsDevice.builder()
                .userId(2L)
                .token("1234")
                .build();

        deviceRepository.saveToken(device);
        List<UsmsDevice> list = jpaDataDeviceRepository.findAll();

        assertThat( deviceRepository.deleteToken(1L) ).isNotEqualTo(0);
    }

    @DisplayName("존재하지 않는 사용자id로 삭제를 시도한경우 0리턴")
    @Test
    public void TestDeleteWithNoExistUseridDevice(){


        assertThat( deviceRepository.deleteToken(2L) ).isEqualTo(0);
    }

    @DisplayName("데이터 저장 테스트")
    @Test
    public void TestInsertWithNoExistUseridDevice(){

        //given
        UsmsDevice device = UsmsDevice.builder()
                .userId(2L)
                .token("1234")
                .build();

        assertThat(device.getId()).isNull();

        //when
        deviceRepository.saveToken(device);

        //then
        assertThat(device.getId()).isNotNull();
    }


    @DisplayName("[findByUserId] : 유저가 소유한 디바이스 정보를 가져온다.")
    @Test
    public void TestFindByUserId(){

        //given
        Long userId = 1L;

        //when
        List<UsmsDevice> devices = deviceRepository.findByUserId(userId);

        //then
        for(UsmsDevice device : devices) {
            assertThat(device.getUserId()).isEqualTo(userId);
        }
    }
}
