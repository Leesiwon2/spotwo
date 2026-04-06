package com.spotwo.spotwo.presentation.record.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateRecordRequest(
    @NotBlank String title,
    String content,
    String thumbnailUrl,
    String regionCode,
    @NotBlank String regionName,
    @NotNull Double latitude,
    @NotNull Double longitude,
    @NotNull LocalDate visitDate
) {}