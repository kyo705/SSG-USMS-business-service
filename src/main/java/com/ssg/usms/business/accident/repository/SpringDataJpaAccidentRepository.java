package com.ssg.usms.business.accident.repository;

import com.ssg.usms.business.accident.constant.AccidentBehavior;
import com.ssg.usms.business.accident.dto.AccidentRegionDto;
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

    List<Accident> findByCctvIdAndIdGreaterThan(Long cctvId, Long accidentId, Pageable pageable);

    @Query(value = "SELECT a.id, a.cctv_id, a.behavior, a.start_timestamp" +
            " FROM store s JOIN cctv c ON s.id = c.store_id JOIN accident a ON c.id = a.cctv_id" +
            " WHERE s.id = :storeId" +
            " AND a.start_timestamp BETWEEN :startTimestamp AND :endTimestamp" +
            " AND a.id > :accidentId" +
            " LIMIT :size", nativeQuery = true)
    List<Accident> findAllByStoreId(@Param("storeId") long storeId,
                                    @Param("startTimestamp") long startTimestamp,
                                    @Param("endTimestamp") long endTimestamp,
                                    @Param("accidentId") long accidentId,
                                    @Param("size") int size);

    @Query(value = "SELECT a.id, a.cctv_id, a.behavior, a.start_timestamp" +
            " FROM store s JOIN cctv c ON s.id = c.store_id JOIN accident a ON c.id = a.cctv_id" +
            " WHERE s.id = :storeId" +
            " AND a.behavior IN :behavior" +
            " AND a.start_timestamp BETWEEN :startTimestamp AND :endTimestamp" +
            " AND a.id > :accidentId" +
            " LIMIT :size", nativeQuery = true)
    List<Accident> findAllByStoreId(@Param("storeId") long storeId,
                                    @Param("behavior") List<Integer> behaviors,
                                    @Param("startTimestamp") long startTimestamp,
                                    @Param("endTimestamp") long endTimestamp,
                                    @Param("accidentId") long accidentId,
                                    @Param("size") int size);

    @Query(value = "SELECT s.id AS storeId, a.behavior AS behavior, COUNT(*) AS count" +
            " FROM store s JOIN cctv c ON s.id = c.store_id JOIN accident a ON c.id = a.cctv_id" +
            " WHERE s.id = :storeId" +
            " AND a.start_timestamp BETWEEN :startTimestamp AND :endTimestamp" +
            " GROUP BY a.behavior", nativeQuery = true)
    List<Object[]> findAccidentStatsByStoreId(@Param("storeId") long storeId,
                                                     @Param("startTimestamp") long startTimestamp,
                                                     @Param("endTimestamp") long endTimestamp);

    @Query(value = "SELECT a.id, s.store_address, a.behavior" +
            " FROM store s JOIN cctv c ON s.id = c.store_id JOIN accident a ON c.id = a.cctv_id" +
            " WHERE a.start_timestamp BETWEEN :startTimestamp AND :endTimestamp" +
            " AND a.id > :accidentId" +
            " LIMIT :size ", nativeQuery = true)
    List<Object[]> findAccidentRegion(@Param("startTimestamp") long startTimestamp,
                                @Param("endTimestamp") long endTimestamp,
                                @Param("accidentId") long accidentId,
                                @Param("size") int size);

    default List<AccidentStatDto> getAccidentStats(long storeId, long startTimestamp, long endTimestamp) {

        List<Object[]> result = findAccidentStatsByStoreId(storeId, startTimestamp, endTimestamp);

        return result.stream()
                .map(row -> new AccidentStatDto(
                        ((Number) row[0]).longValue(),
                        AccidentBehavior.valueOfCode((short) row[1]),
                        ((Number) row[2]).longValue(),
                        new Timestamp(startTimestamp).toLocalDateTime()
                                .toLocalDate()
                                .format(DateTimeFormatter.ofPattern("yy-MM-dd")),
                        new Timestamp(endTimestamp).toLocalDateTime()
                                .toLocalDate()
                                .format(DateTimeFormatter.ofPattern("yy-MM-dd"))
                        )
                )
                .collect(Collectors.toList());
    }

    default List<AccidentRegionDto> getAccidentRegion(long startTimestamp, long endTimestamp, long accidentId, int size) {

        List<Object[]> result = findAccidentRegion(startTimestamp, endTimestamp, accidentId, size);

        return result.stream()
                .map(row -> AccidentRegionDto.builder()
                            .accidentId(((Number) row[0]).longValue())
                            .storeAddress((String) row[1])
                            .behavior(AccidentBehavior.valueOfCode((short) row[2]))
                            .build())
                .collect(Collectors.toList());
    }
}
