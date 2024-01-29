package com.ssg.usms.business.device;


import com.ssg.usms.business.device.repository.DeviceRepository;
import com.ssg.usms.business.device.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceTest {

    @Mock
    private DeviceRepository repository;
    @InjectMocks
    private DeviceService deviceService;

    @Test
    public void SuccessSaveToken(){

        Long userid =1L;
        String token = "asdf";

        doNothing().when(repository).saveToken(any());
        assertDoesNotThrow(() -> deviceService.saveToken(token,userid));
    }

    @Test
    public void SuccessDeleteToken(){

        Long userid =1L;
        String token = "asdf";

        given(repository.deleteToken(any())).willReturn(1);
        assertDoesNotThrow(() -> deviceService.deleteToken(userid));
    }



}
