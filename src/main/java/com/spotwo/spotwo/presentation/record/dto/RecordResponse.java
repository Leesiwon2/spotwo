package com.spotwo.spotwo.presentation.record.dto;

import com.spotwo.spotwo.application.record.dto.RecordResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RecordResponse(
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
  public static RecordResponse from(RecordResult result) {
    return new RecordResponse(
        result.id(), result.coupleId(), result.writerId(),
        result.regionCode(), result.regionName(),
        result.latitude(), result.longitude(),
        result.title(), result.content(), result.thumbnailUrl(),
        result.visitDate(), result.season(), result.weatherTag(),
        result.recordType(), result.photoUrls(), result.createdAt()
    );
  }
}