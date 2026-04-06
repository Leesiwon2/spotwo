package com.spotwo.spotwo.application.record.dto;

import java.time.LocalDate;

public record UpdateRecordCommand(
    Long recordId,
    Long userId,
    String title,
    String content,
    String thumbnailUrl,
    String regionCode,
    String regionName,
    Double latitude,
    Double longitude,
    LocalDate visitDate
) {}