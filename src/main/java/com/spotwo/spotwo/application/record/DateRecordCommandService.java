package com.spotwo.spotwo.application.record;

import com.spotwo.spotwo.application.record.dto.*;
import com.spotwo.spotwo.domain.couple.Couple;
import com.spotwo.spotwo.domain.couple.CoupleRepository;
import com.spotwo.spotwo.domain.record.*;
import com.spotwo.spotwo.domain.user.UserId;
import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DateRecordCommandService {

  private final DateRecordRepository dateRecordRepository;
  private final CoupleRepository coupleRepository;
  private final WeatherTagService weatherTagService;

  public RecordResult create(CreateRecordCommand command) {

    Couple couple = coupleRepository
        .findMatchedCoupleByUserId(new UserId(command.writerId()))
        .orElseThrow(() -> new CustomException(ErrorCode.COUPLE_NOT_FOUND));

    DateRecord recordData = DateRecord.create(
        couple.getId(),
        new UserId(command.writerId()),
        command.regionCode(),
        command.regionName(),
        command.latitude(),
        command.longitude(),
        command.title(),
        command.content(),
        command.thumbnailUrl(),
        command.visitDate(),
        command.recordType()
    );

    // 날씨 자동태깅 (VISIT일 때만)
    if (command.recordType() == RecordType.VISIT) {
      String weatherTag = weatherTagService.getWeatherTag(
          command.latitude(), command.longitude(), command.visitDate());
      recordData.setWeatherTag(weatherTag);
    }

    DateRecord saved = dateRecordRepository.save(recordData);
    return RecordResult.from(saved);
  }

  public RecordResult update(UpdateRecordCommand command) {
    DateRecord recordData = dateRecordRepository.findById(new RecordId(command.recordId()))
        .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

    // 작성자 본인만 수정 가능
    if (!recordData.isOwnedBy(new UserId(command.userId()))) {
      throw new CustomException(ErrorCode.FORBIDDEN);
    }

    recordData.update(
        command.title(), command.content(), command.thumbnailUrl(),
        command.regionCode(), command.regionName(),
        command.latitude(), command.longitude(), command.visitDate()
    );

    return RecordResult.from(dateRecordRepository.save(recordData));
  }

  public void delete(Long recordId, Long userId) {
    DateRecord recordData = dateRecordRepository.findById(new RecordId(recordId))
        .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

    if (!recordData.isOwnedBy(new UserId(userId))) {
      throw new CustomException(ErrorCode.FORBIDDEN);
    }

    dateRecordRepository.deleteById(new RecordId(recordId));
  }

  public RecordResult toggleWishlist(Long recordId, Long userId) {
    DateRecord recordData = dateRecordRepository.findById(new RecordId(recordId))
        .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

    if (!recordData.isOwnedBy(new UserId(userId))) {
      throw new CustomException(ErrorCode.FORBIDDEN);
    }

    if (recordData.getRecordType() == RecordType.VISIT) {
      recordData.toggleToWishlist();
    } else {
      recordData.toggleToVisit();
    }

    return RecordResult.from(dateRecordRepository.save(recordData));
  }
}