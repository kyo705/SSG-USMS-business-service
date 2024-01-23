package com.ssg.usms.business.cctv.service;

import com.ssg.usms.business.cctv.CctvTestSetup;
import com.ssg.usms.business.cctv.dto.CctvDto;
import com.ssg.usms.business.cctv.exception.NotExistingCctvException;
import com.ssg.usms.business.cctv.repository.Cctv;
import com.ssg.usms.business.cctv.repository.CctvRepository;
import com.ssg.usms.business.store.service.StoreService;
import com.ssg.usms.business.video.exception.NotExistingStreamKeyException;
import com.ssg.usms.business.video.repository.StreamKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CctvServiceTest {

    private CctvService cctvService;


    @Mock
    private StoreService storeService;
    @Mock
    private CctvRepository mockCctvRepository;
    @Mock
    private StreamKeyRepository mockStreamKeyRepository;

    @BeforeEach
    public void setup() {
        cctvService = new CctvService(mockCctvRepository, mockStreamKeyRepository);
    }

    @DisplayName("[createCctv] : cctv 생성 테스트")
    @Test
    public void testCreateCctv() {

        //given
        Long storeId = 1L;
        String name = "cctv 별칭";

        //when
        cctvService.createCctv(storeId, name);

        //then
        verify(mockCctvRepository, times(1)).save(any());
    }

    @DisplayName("[findById] : cctv id로 조회 테스트")
    @ValueSource(booleans = {true, false})
    @ParameterizedTest
    public void testFindById(boolean isConnected) {

        //given
        Long id = 1L;
        String streamKey = "스트림키";

        Cctv cctv = new Cctv();
        cctv.setId(id);
        cctv.setStoreId(1L);
        cctv.setName("cctv 명칭");
        cctv.setExpired(false);
        cctv.setStreamKey(streamKey);

        given(mockCctvRepository.findById(id)).willReturn(cctv);
        given(mockStreamKeyRepository.isExistingStreamKey(streamKey)).willReturn(isConnected);

        //when
        CctvDto cctvDto = cctvService.findById(id);

        //then
        verify(mockCctvRepository, times(1)).findById(id);
        verify(mockStreamKeyRepository, times(1)).isExistingStreamKey(streamKey);
        assertThat(cctvDto.getIsConnected()).isEqualTo(isConnected);
    }

    @DisplayName("[findById] : 존재하지 않는 cctv id로 조회 테스트")
    @Test
    public void testFindByIdWithNotExistingCctv() {

        //given
        Long id = 1L;
        String streamKey = "스트림키";

        given(mockCctvRepository.findById(id)).willThrow(NotExistingCctvException.class);

        //when & then
        assertThrows(NotExistingCctvException.class, () -> cctvService.findById(id));

        verify(mockCctvRepository, times(1)).findById(id);
        verify(mockStreamKeyRepository, times(0)).isExistingStreamKey(any());
    }

    @DisplayName("[findByStreamKey] : streamKey로 cctv 조회 테스트")
    @ValueSource(booleans = {true, false})
    @ParameterizedTest
    public void testFindByStreamKey(boolean isConnected) {

        //given
        Long id = 1L;
        String streamKey = "스트림키";

        Cctv cctv = new Cctv();
        cctv.setId(id);
        cctv.setStoreId(1L);
        cctv.setName("cctv 명칭");
        cctv.setExpired(false);
        cctv.setStreamKey(streamKey);

        given(mockCctvRepository.findByStreamKey(streamKey)).willReturn(cctv);
        given(mockStreamKeyRepository.isExistingStreamKey(streamKey)).willReturn(isConnected);

        //when
        CctvDto cctvDto = cctvService.findByStreamKey(streamKey);

        //then
        verify(mockCctvRepository, times(1)).findByStreamKey(streamKey);
        verify(mockStreamKeyRepository, times(1)).isExistingStreamKey(streamKey);
        assertThat(cctvDto.getIsConnected()).isEqualTo(isConnected);
    }

    @DisplayName("[findByStreamKey] : 존재하지 않는 streamKey로 cctv 조회 테스트")
    @Test
    public void testFindByStreamKeyWithNotExistingStreamKey() {

        //given
        String streamKey = "스트림키";

        given(mockCctvRepository.findByStreamKey(streamKey)).willThrow(NotExistingStreamKeyException.class);

        //when
        assertThrows(NotExistingStreamKeyException.class, () -> cctvService.findByStreamKey(streamKey));

        //then
        verify(mockCctvRepository, times(1)).findByStreamKey(streamKey);
        verify(mockStreamKeyRepository, times(0)).isExistingStreamKey(any());
    }

    @DisplayName("[findAllByStoreId] : 특정 storeId에 해당하는 cctv들 조회 테스트")
    @ValueSource(booleans = {true, false})
    @ParameterizedTest
    public void testFindAllByStoreId(boolean isConnected) {

        //given
        Long storeId = 1L;
        int offset = 0;
        int size = 10;

        given(mockCctvRepository.findByStoreId(storeId, offset, size)).willReturn(CctvTestSetup.getCctvList(storeId));
        given(mockStreamKeyRepository.isExistingStreamKey(any())).willReturn(isConnected);

        //when
        List<CctvDto> result = cctvService.findAllByStoreId(storeId, offset, size);

        //then
        for(CctvDto cctvDto : result) {
            assertThat(cctvDto.getStoreId()).isEqualTo(storeId);
            assertThat(cctvDto.getIsConnected()).isEqualTo(isConnected);
        }
        verify(mockCctvRepository, times(1)).findByStoreId(storeId, offset, size);
        verify(mockStreamKeyRepository, times(result.size())).isExistingStreamKey(any());
    }

    @DisplayName("[changeCctvName] : cctv 명칭 수정 테스트")
    @Test
    public void testChangeCctvName() {

        //given
        Long cctvId = 1L;
        String cctvName = "변경할 cctv 명칭";

        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setStoreId(1L);
        cctv.setName("cctv 명칭");
        cctv.setExpired(false);
        cctv.setStreamKey("streamKey");

        given(mockCctvRepository.findById(cctvId)).willReturn(cctv);

        //when
        cctvService.changeCctvName(cctvId, cctvName);

        //then
        assertThat(cctv.getName()).isEqualTo(cctvName);
        verify(mockCctvRepository, times(1)).findById(cctvId);
        verify(mockCctvRepository, times(1)).update(any());
    }

    @DisplayName("[changeCctvName] : 존재하지 않는 cctv id로 cctv 명칭 수정 테스트")
    @Test
    public void testChangeCctvNameWithNotExistingCctv() {

        //given
        Long cctvId = 1L;
        String cctvName = "변경할 cctv 명칭";

        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setStoreId(1L);
        cctv.setName("cctv 명칭");
        cctv.setExpired(false);
        cctv.setStreamKey("streamKey");

        given(mockCctvRepository.findById(cctvId)).willThrow(NotExistingCctvException.class);

        //when
        assertThrows(NotExistingCctvException.class, () -> cctvService.changeCctvName(cctvId, cctvName));

        //then
        assertThat(cctv.getName()).isNotEqualTo(cctvName);
        verify(mockCctvRepository, times(1)).findById(cctvId);
        verify(mockCctvRepository, times(0)).update(any());
    }

    @DisplayName("[changeCctvName] : cctv 명칭 수정 테스트")
    @Test
    public void testDeleteCctv() {

        //given
        Long cctvId = 1L;

        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setStoreId(1L);
        cctv.setName("cctv 명칭");
        cctv.setExpired(false);
        cctv.setStreamKey("streamKey");

        given(mockCctvRepository.findById(cctvId)).willReturn(cctv);

        //when
        cctvService.deleteCctv(cctvId);

        //then
        verify(mockCctvRepository, times(1)).findById(cctvId);
        verify(mockCctvRepository, times(1)).delete(any());
    }

    @DisplayName("[changeCctvName] : 존재하지 않는 cctv id로 cctv 명칭 수정 테스트")
    @Test
    public void testDeleteCctvWithNotExistingCctv() {

        //given
        Long cctvId = 1L;

        Cctv cctv = new Cctv();
        cctv.setId(cctvId);
        cctv.setStoreId(1L);
        cctv.setName("cctv 명칭");
        cctv.setExpired(false);
        cctv.setStreamKey("streamKey");

        given(mockCctvRepository.findById(cctvId)).willThrow(NotExistingCctvException.class);

        //when
        assertThrows(NotExistingCctvException.class, () -> cctvService.deleteCctv(cctvId));

        //then
        verify(mockCctvRepository, times(1)).findById(cctvId);
        verify(mockCctvRepository, times(0)).delete(any());
    }
}
