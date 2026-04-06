package com.spotwo.spotwo.application.record;

import com.spotwo.spotwo.application.record.dto.*;
import com.spotwo.spotwo.domain.couple.Couple;
import com.spotwo.spotwo.domain.couple.CoupleRepository;
import com.spotwo.spotwo.domain.record.*;
import com.spotwo.spotwo.domain.user.UserId;
import com.spotwo.spotwo.domain.user.UserRepository;
import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DateRecordQueryService {

  private final DateRecordRepository dateRecordRepository;
  private final CoupleRepository coupleRepository;
  private final UserRepository userRepository;

  // 지도 핀 전체 조회
  public List<MapPinResult> getMapPins(Long userId) {
    Couple couple = coupleRepository.findMatchedCoupleByUserId(new UserId(userId))
        .orElseThrow(() -> new CustomException(ErrorCode.COUPLE_NOT_FOUND));

    List<DateRecord> records = dateRecordRepository
        .findByCoupleId(couple.getId());

    return records.stream()
        .map(recordData -> {
          String writerNickname = userRepository
              .findById(recordData.getWriterId())
              .map(u -> u.getNickname())
              .orElse("알 수 없음");

          return new MapPinResult(
              recordData.getId().value(),
              recordData.getRegionCode(),
              recordData.getRegionName(),
              recordData.getLatitude(),
              recordData.getLongitude(),
              recordData.getThumbnailUrl(),
              recordData.getRecordType().name(),
              recordData.getSeason().toKorean(),
              writerNickname
          );
        })
        .toList();
  }

  // 기록 상세 조회
  public RecordResult getRecord(Long recordId, Long userId) {
    DateRecord recordData = dateRecordRepository.findById(new RecordId(recordId))
        .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

    Couple couple = coupleRepository.findMatchedCoupleByUserId(new UserId(userId))
        .orElseThrow(() -> new CustomException(ErrorCode.COUPLE_NOT_FOUND));

    // 커플 소유 기록인지 확인
    if (!recordData.isOwnedByCouple(couple.getId())) {
      throw new CustomException(ErrorCode.FORBIDDEN);
    }

    return RecordResult.from(recordData);
  }

  // 통계 조회
  public RecordStatsResult getStats(Long userId) {
    Couple couple = coupleRepository.findMatchedCoupleByUserId(new UserId(userId))
        .orElseThrow(() -> new CustomException(ErrorCode.COUPLE_NOT_FOUND));

    long visited = dateRecordRepository.countVisitByCoupleId(couple.getId());
    long wishlist = dateRecordRepository.findByCoupleId(couple.getId())
        .stream()
        .filter(r -> r.getRecordType() == RecordType.WISHLIST)
        .count();

    String message = String.format("우리 %d개 지역 정복! 🎉", visited);

    return new RecordStatsResult(visited, wishlist, message);
  }
}