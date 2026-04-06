package com.spotwo.spotwo.application.record.dto;

public record RecordStatsResult(
    long totalVisited,
    long wishlistCount,
    String message  // "우리 N개 지역 정복!"
) {}