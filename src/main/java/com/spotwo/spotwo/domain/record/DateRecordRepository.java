package com.spotwo.spotwo.domain.record;

import com.spotwo.spotwo.domain.couple.CoupleId;

import java.util.List;
import java.util.Optional;

public interface DateRecordRepository {
  DateRecord save(DateRecord recordData);
  Optional<DateRecord> findById(RecordId id);
  List<DateRecord> findByCoupleId(CoupleId coupleId);
  void deleteById(RecordId id);
  long countVisitByCoupleId(CoupleId coupleId);
}