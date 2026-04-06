package com.spotwo.spotwo.application.record.dto;

import com.spotwo.spotwo.domain.record.DateRecord;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RecordResult(
    Long id,
    Long coupleId,
    Long writerId,
    String regionCode,
    String regionName,
    Double latitude,
    Double longitude,
    String title,
    String content,
    String thumbnailUrl,
    LocalDate visitDate,
    String season,
    String weatherTag,
    String recordType,
    List<String> photoUrls,
    LocalDateTime createdAt
) {
  public static RecordResult from(DateRecord recordData) {
    return new RecordResult(
        recordData.getId().value(),
        recordData.getCoupleId().value(),
        recordData.getWriterId().value(),
        recordData.getRegionCode(),
        recordData.getRegionName(),
        recordData.getLatitude(),
        recordData.getLongitude(),
        recordData.getTitle(),
        recordData.getContent(),
        recordData.getThumbnailUrl(),
        recordData.getVisitDate(),
        recordData.getSeason().toKorean(),
        recordData.getWeatherTag(),
        recordData.getRecordType().name(),
        recordData.getPhotoUrls(),
        recordData.getCreatedAt()
    );
  }
}