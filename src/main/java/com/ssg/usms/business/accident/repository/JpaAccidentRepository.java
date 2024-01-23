package com.ssg.usms.business.accident.repository;

import com.ssg.usms.business.accident.dto.AccidentStatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
