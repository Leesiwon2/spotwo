package com.spotwo.spotwo.infrastructure.persistence.record;

import com.spotwo.spotwo.domain.couple.CoupleId;
import com.spotwo.spotwo.domain.record.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DateRecordRepositoryImpl implements DateRecordRepository {

  private final DateRecordJpaRepository jpaRepository;

  @Override
  public DateRecord save(DateRecord recordData) {
    return jpaRepository.save(
        DateRecordJpaEntity.fromDomain(recordData)).toDomain();
  }

  @Override
  public Optional<DateRecord> findById(RecordId id) {
    return jpaRepository.findById(id.value())
        .map(DateRecordJpaEntity::toDomain);
  }

  @Override
  public List<DateRecord> findByCoupleId(CoupleId coupleId) {
    return jpaRepository.findByCoupleId(coupleId.value())
        .stream()
        .map(DateRecordJpaEntity::toDomain)
        .toList();
  }

  @Override
  public void deleteById(RecordId id) {
    jpaRepository.deleteById(id.value());
  }

  @Override
  public long countVisitByCoupleId(CoupleId coupleId) {
    return jpaRepository.countVisitByCoupleId(coupleId.value());
  }
}