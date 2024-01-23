package com.ssg.usms.business.accident.repository;

import com.ssg.usms.business.accident.dto.AccidentStatDto;

import java.util.List;

public interface AccidentRepository {

    void save(Accident accident);

    List<Accident> findAllByCctvId(Long cctvId, int offset, int size);

    List<AccidentStatDto> findAccidentStats(long storeId, long startTimestamp, long endTimestamp);
}
