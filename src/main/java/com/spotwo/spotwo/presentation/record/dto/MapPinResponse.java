package com.spotwo.spotwo.presentation.record.dto;

import com.spotwo.spotwo.application.record.dto.MapPinResult;

public record MapPinResponse(
    Long recordId,
    String regionCode,
    String regionName,
    Double latitude,
    Double longitude,
    String thumbnailUrl,
    String recordType,
    String season,
    String writerNickname
) {
  public static MapPinResponse from(MapPinResult result) {
    return new MapPinResponse(
        result.recordId(), result.regionCode(), result.regionName(),
        result.latitude(), result.longitude(), result.thumbnailUrl(),
        result.recordType(), result.season(), result.writerNickname()
    );
  }
}