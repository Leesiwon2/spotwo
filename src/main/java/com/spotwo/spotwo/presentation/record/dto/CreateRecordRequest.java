package com.spotwo.spotwo.presentation.record.dto;

import com.spotwo.spotwo.domain.record.RecordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateRecordRequest(
    @NotBlank(message = "지역명을 입력해주세요.")
    String regionName,

    String regionCode,

    @NotNull(message = "위도를 입력해주세요.")
    Double latitude,

    @NotNull(message = "경도를 입력해주세요.")
    Double longitude,

    @NotBlank(message = "제목을 입력해주세요.")
    String title,

    String content,
    String thumbnailUrl,

    @NotNull(message = "방문 날짜를 입력해주세요.")
    LocalDate visitDate,

    @NotNull(message = "기록 타입을 입력해주세요.")
    RecordType recordType
) {}