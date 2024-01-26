package com.ssg.usms.business.device;

import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.device.repository.DeviceRepository;
import com.ssg.usms.business.device.repository.SpringJpaDataDeviceRepository;
import com.ssg.usms.business.device.repository.UserDevice;
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
public class deviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SpringJpaDataDeviceRepository jpaDataDeviceRepository;
    @DisplayName("성공적으로 삭제한 경우 1 리턴")
    @Test
    public void TestDeleteDevice(){
        UserDevice device = UserDevice.builder()
                .userid(1L)
                .token("1234")
                .build();

        deviceRepository.saveToken(device);
        List<UserDevice> list = jpaDataDeviceRepository.findAll();

        assertThat( deviceRepository.deleteToken(1L) ).isEqualTo(1);
    }

    @DisplayName("존재하지 않는 사용자id로 삭제를 시도한경우 0리턴")
    @Test
    public void TestDeleteWithNoExistUseridDevice(){


        assertThat( deviceRepository.deleteToken(1L) ).isEqualTo(0);
    }

    @DisplayName("성공적으로 데이터를 insert한경우 findall의 길이가 1이다.")
    @Test
    public void TestInsertWithNoExistUseridDevice(){

        UserDevice device = UserDevice.builder()
                .userid(1L)
                .token("1234")
                .build();

        deviceRepository.saveToken(device);
        List<UserDevice> list = jpaDataDeviceRepository.findAll();

        assertThat( list.size() ).isEqualTo(1);
    }


}
