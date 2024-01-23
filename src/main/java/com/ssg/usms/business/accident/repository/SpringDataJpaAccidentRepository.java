package com.ssg.usms.business.accident.repository;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.dto.AccidentStatDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public interface SpringDataJpaAccidentRepository extends JpaRepository<Accident, Long> {

    List<Accident> findByCctvId(Long cctvId, Pageable pageable);

    @Query(value = "SELECT s.id AS storeId, a.behavior AS behavior, COUNT(*) AS count" +
            " FROM store s JOIN cctv c ON s.id = c.store_id JOIN accident a ON c.id = a.cctv_id" +
            " WHERE s.id = :storeId AND a.start_timestamp BETWEEN :startTimestamp AND :endTimestamp" +
            " GROUP BY a.behavior", nativeQuery = true)
    List<Object[]> findAccidentStatsByStoreId(@Param("storeId") long storeId,
                                                     @Param("startTimestamp") long startTimestamp,
                                                     @Param("endTimestamp") long endTimestamp);

    default List<AccidentStatDto> getAccidentStats(long storeId, long startTimestamp, long endTimestamp) {

        List<Object[]> result = findAccidentStatsByStoreId(storeId, startTimestamp, endTimestamp);

        return result.stream()
                .map(row -> new AccidentStatDto(
                        ((Number) row[0]).longValue(),
                        AccidentBehavior.valueOfCode((short) row[1]),
                        ((Number) row[2]).longValue(),
                        new Timestamp(startTimestamp).toLocalDateTime()
                                .toLocalDate()
                                .format(DateTimeFormatter.ofPattern("yy-MM")),
                        new Timestamp(endTimestamp).toLocalDateTime()
                                .toLocalDate()
                                .format(DateTimeFormatter.ofPattern("yy-MM"))
                        )
                )
                .collect(Collectors.toList());
    }
}
