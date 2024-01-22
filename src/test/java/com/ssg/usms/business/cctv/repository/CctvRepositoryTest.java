package com.ssg.usms.business.cctv.repository;

import com.ssg.usms.business.cctv.exception.NotExistingCctvException;
import com.ssg.usms.business.video.exception.NotExistingStreamKeyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional(readOnly = true)
@ActiveProfiles("test")
@SpringBootTest
public class CctvRepositoryTest {

    @Autowired
    private CctvRepository cctvRepository;

    @DisplayName("[save] : 중복되지 않은 스트림 키로 cctv 저장 테스트")
    @Test
    public void testSaveWithUniqueStreamKey() {

        //given
        Cctv cctv = new Cctv();
        cctv.setStoreId(1L);
        cctv.setName("cctv 3");
        cctv.setStreamKey("74d18bfc-14c5-46d2-a1a8-1eb627618859");
        cctv.setExpired(false);

        assertThat(cctv.getId()).isNull();

        //when
        Cctv savedCctv = cctvRepository.save(cctv);

        assertThat(cctv.getId()).isNotNull();
        assertThat(cctv).isEqualTo(savedCctv);
    }

    @DisplayName("[save] : 이미 저장된 스트림 키로 cctv 저장 테스트")
    @Test
    public void testSaveWithDuplicatedStreamKey() {

        //given
        Cctv cctv = new Cctv();
        cctv.setStoreId(1L);
        cctv.setName("cctv 3");
        cctv.setStreamKey("74d18bfc-14c5-46d2-a1a8-1eb627918859"); // 이미 저장되어있는 키
        cctv.setExpired(false);

        assertThat(cctv.getId()).isNull();

        //when & then
        assertThrows(DataIntegrityViolationException.class, () -> cctvRepository.save(cctv));
    }

    @DisplayName("[findById] : 존재하는 cctv id 로 cctv 조회 테스트")
    @Test
    public void testFindByIdWithExistingCctvId() {

        //given
        Long cctvId = 1L;

        //when
        Cctv savedCctv = cctvRepository.findById(cctvId);

        //then
        assertThat(savedCctv.getId()).isEqualTo(cctvId);
    }

    @DisplayName("[findById] : 존재하지 않는 cctv id 로 cctv 조회 테스트")
    @Test
    public void testFindByIdWithNotExistingCctvId() {

        //given
        Long cctvId = 1315L;

        //when & then
        assertThrows(NotExistingCctvException.class, () -> cctvRepository.findById(cctvId));
    }

    @DisplayName("[findByStoreId] : store id 로 cctv 조회 테스트")
    @Test
    public void testFindByStoreIdWithExistingStoreId() {

        //given
        Long storeId = 1L;
        int offset = 0;
        int size = 10;

        //when
        List<Cctv> result = cctvRepository.findByStoreId(storeId, offset, size);

        //then
        assertThat(result.size()).isLessThanOrEqualTo(size);
        for(Cctv cctv : result) {
            assertThat(cctv.getStoreId()).isEqualTo(storeId);
        }
    }

    @DisplayName("[findByStoreId] : 잘못된 offset 값으로 cctv 조회 테스트")
    @Test
    public void testFindByStoreIdWithInvalidOffset() {

        //given
        Long storeId = 1L;
        int invalidOffset = -1;    // INVALID
        int size = 10;

        //when & then
        assertThrows(InvalidDataAccessApiUsageException.class ,
                () -> cctvRepository.findByStoreId(storeId, invalidOffset, size));

    }

    @DisplayName("[findByStoreId] : 잘못된 size 값으로 cctv 조회 테스트")
    @ValueSource(ints = {0, -1, -5})
    @ParameterizedTest
    public void testFindByStoreIdWithInvalidSize(int invalidSize) {

        //given
        Long storeId = 1L;
        int offset = 0;

        //when & then
        assertThrows(InvalidDataAccessApiUsageException.class ,
                () -> cctvRepository.findByStoreId(storeId, offset, invalidSize));

    }

    @DisplayName("[findByStreamKey] : 존재하는 StreamKey로 조회")
    @Test
    public void testFindByStreamKeyWithExistingStreamKey() {

        //given
        String streamKey = "74d18bfc-14c5-46d2-a1a8-1eb627918859"; // 존재하는 스트림 키

        //when
        Cctv cctv = cctvRepository.findByStreamKey(streamKey);

        //then
        assertThat(cctv.getStreamKey()).isEqualTo(streamKey);
    }

    @DisplayName("[findByStreamKey] : 존재하지 않는 StreamKey로 조회")
    @Test
    public void testFindByStreamKeyWithNotExistingStreamKey() {

        //given
        String streamKey = "not existing key"; // 존재하는 스트림 키

        //when & then
        assertThrows(NotExistingStreamKeyException.class,
                () -> cctvRepository.findByStreamKey(streamKey));

    }

    @DisplayName("[update] : cctv를 업데이트")
    @Test
    public void testUpdate() {

        //given
        Long cctvId = 1L;
        String changingCctvName = "변경할 cctv 이름";

        Cctv cctv = cctvRepository.findById(cctvId);
        cctv.setName(changingCctvName);

        //when
        cctvRepository.update(cctv);

        //then
        Cctv updatedCctv = cctvRepository.findById(cctvId);
        assertThat(updatedCctv.getName()).isEqualTo(changingCctvName);
    }

    @DisplayName("[delete] : 존재하는 cctv를 삭제")
    @Test
    public void testDeleteWithExistingCctv() {

        //given
        Long cctvId = 1L;

        Cctv cctv = cctvRepository.findById(cctvId);
        assertThat(cctv).isNotNull();

        //when
        cctvRepository.delete(cctv);

        //then
        assertThrows(NotExistingCctvException.class, () -> cctvRepository.findById(cctvId));

    }

    @DisplayName("[delete] : 존재하지 않는 cctv를 삭제")
    @Test
    public void testDeleteWithNotExistingCctv() {

        //given
        Long notExistingCctvId = 11351L;

        Cctv cctv = new Cctv();
        cctv.setId(notExistingCctvId);
        cctv.setStoreId(1L);
        cctv.setName("cctv 3");
        cctv.setStreamKey("74d18bfc-17c5-46d2-a1a8-1eb627518859");
        cctv.setExpired(false);

        assertThrows(NotExistingCctvException.class, () -> cctvRepository.findById(cctv.getId()));

        //when
        cctvRepository.delete(cctv);

        //then
        assertThrows(NotExistingCctvException.class, () -> cctvRepository.findById(cctv.getId()));

    }
}
