package com.ssg.usms.business.cctv.repository;

import com.ssg.usms.business.cctv.exception.NotExistingCctvException;
import com.ssg.usms.business.video.exception.NotExistingStreamKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaCctvRepository implements CctvRepository {

    private final SpringDataJpaCctvRepository springDataJpaCctvRepository;

    @Override
    public Cctv save(Cctv cctv) {

        return springDataJpaCctvRepository.save(cctv);
    }

    @Override
    public Cctv findById(Long id) {

        return springDataJpaCctvRepository.findById(id)
                .orElseThrow(NotExistingCctvException::new);
    }

    @Override
    public List<Cctv> findByStoreId(Long storeId, int offset, int size) {

        return springDataJpaCctvRepository.findByStoreId(storeId, PageRequest.of(offset, size));
    }

    @Override
    public Cctv findByStreamKey(String streamKey) {

        return springDataJpaCctvRepository.findByStreamKey(streamKey)
                .orElseThrow(NotExistingStreamKeyException::new);
    }

    @Override
    public void update(Cctv cctv) {

    }

    @Override
    public void delete(Cctv cctv) {

        springDataJpaCctvRepository.delete(cctv);
    }
}
