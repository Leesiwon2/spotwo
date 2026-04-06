package com.spotwo.spotwo.application.record.dto;

import com.spotwo.spotwo.domain.record.RecordType;
import java.time.LocalDate;

public record CreateRecordCommand(
    Long writerId,
    String regionCode,
    String regionName,
    Double latitude,
    Double longitude,
    String title,
    String content,
    String thumbnailUrl,
    LocalDate visitDate,
    RecordType recordType
) {}