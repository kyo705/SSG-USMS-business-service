package com.ssg.usms.business.accident.repository;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.dto.AccidentRegionDto;
import com.ssg.usms.business.accident.dto.AccidentStatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaAccidentRepository implements AccidentRepository {

    private final SpringDataJpaAccidentRepository springDataJpaAccidentRepository;

    @Override
    public void save(Accident accident) {

        springDataJpaAccidentRepository.save(accident);
    }

    @Override
    public List<Accident> findAllByCctvId(Long cctvId, int offset, int size) {

        return springDataJpaAccidentRepository.findByCctvId(cctvId, PageRequest.of(offset, size));
    }

    @Override
    public List<AccidentStatDto> findAccidentStats(long storeId, long startTimestamp, long endTimestamp) {

        return springDataJpaAccidentRepository.getAccidentStats(storeId, startTimestamp, endTimestamp);
    }

    @Override
    public List<Accident> findAllByStoreId(long storeId, long startTimestamp, long endTimestamp, int offset, int size) {

        return springDataJpaAccidentRepository
                .findAllByStoreId(storeId, startTimestamp, endTimestamp, offset, size);
    }

    @Override
    public List<Accident> findAllByStoreId(long storeId, List<AccidentBehavior> behavior, long startTimestamp, long endTimestamp, int offset, int size) {

        List<Integer> behaviorCodes = behavior.stream().map(AccidentBehavior::getCode).collect(Collectors.toList());

        return springDataJpaAccidentRepository
                .findAllByStoreId(storeId, behaviorCodes, startTimestamp, endTimestamp, offset, size);
    }

    @Override
    public List<AccidentRegionDto> findAccidentRegion(long startTimestamp, long endTimestamp, int offset, int size) {

        return springDataJpaAccidentRepository.getAccidentRegion(startTimestamp, endTimestamp, offset, size);
    }


}
