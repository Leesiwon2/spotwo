package com.spotwo.spotwo.application.record.dto;

public record MapPinResult(
    Long recordId,
    String regionCode,
    String regionName,
    Double latitude,
    Double longitude,
    String thumbnailUrl,
    String recordType,
    String season,
    String writerNickname
) {}