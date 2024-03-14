package com.ssg.usms.business.cctv.service;

import com.ssg.usms.business.cctv.dto.CctvDto;
import com.ssg.usms.business.cctv.exception.NotOwnedCctvException;
import com.ssg.usms.business.cctv.repository.Cctv;
import com.ssg.usms.business.cctv.repository.CctvRepository;
import com.ssg.usms.business.video.repository.StreamKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CctvService {

    private final CctvRepository cctvRepository;
    private final StreamKeyRepository streamKeyRepository;

    @Transactional
    public CctvDto createCctv(Long storeId, String name) {

        Cctv cctv = Cctv.init(storeId, name);

        return new CctvDto(cctvRepository.save(cctv));
    }

    @Transactional(readOnly = true)
    public CctvDto findById(Long id) {

        CctvDto cctv = new CctvDto(cctvRepository.findById(id));
        cctv.setIsConnected(streamKeyRepository.isExistingStreamKey(cctv.getCctvStreamKey()));

        return cctv;
    }

    @Transactional(readOnly = true)
    public CctvDto findByStreamKey(String streamKey) {

        CctvDto cctv = new CctvDto(cctvRepository.findByStreamKey(streamKey));
        cctv.setIsConnected(streamKeyRepository.isExistingStreamKey(cctv.getCctvStreamKey()));

        return cctv;
    }

    @Transactional(readOnly = true)
    public List<CctvDto> findAllByStoreId(long storeId, long cctvId, int size) {

        return cctvRepository.findByStoreId(storeId, cctvId, size)
                .stream()
                .map(cctv -> {
                    CctvDto cctvDto = new CctvDto(cctv);
                    cctvDto.setIsConnected(streamKeyRepository.isExistingStreamKey(cctv.getStreamKey()));

                    return cctvDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void changeCctvName(Long cctvId, String cctvName) {

        Cctv cctv = cctvRepository.findById(cctvId);
        cctv.changeCctvName(cctvName);

        cctvRepository.update(cctv);
    }

    @Transactional
    public void deleteCctv(Long cctvId) {

        Cctv cctv = cctvRepository.findById(cctvId);
        cctvRepository.delete(cctv);
    }

    @Transactional(readOnly = true)
    public void validateOwnedCctv(Long storeId, Long cctvId) {

        Cctv cctv = cctvRepository.findById(cctvId);
        if(cctv.getStoreId() != storeId) {
            throw new NotOwnedCctvException();
        }
    }
}
