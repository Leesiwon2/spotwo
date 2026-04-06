package com.spotwo.spotwo.infrastructure.persistence.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DateRecordJpaRepository
    extends JpaRepository<DateRecordJpaEntity, Long> {

  List<DateRecordJpaEntity> findByCoupleId(Long coupleId);

  @Query("""
        SELECT COUNT(r) FROM DateRecordJpaEntity r
        WHERE r.coupleId = :coupleId
        AND r.recordType = 'VISIT'
    """)
  long countVisitByCoupleId(@Param("coupleId") Long coupleId);
}