package com.spotwo.spotwo.presentation.couple.dto;

import com.spotwo.spotwo.application.couple.dto.CoupleResult;

import java.time.LocalDateTime;

public record CoupleResponse(
    Long coupleId,
    Long inviterId,
    Long inviteeId,
    String status,
    LocalDateTime matchedAt
) {
  public static CoupleResponse from(CoupleResult result) {
    return new CoupleResponse(
        result.coupleId(),
        result.inviterId(),
        result.inviteeId(),
        result.status(),
        result.matchedAt()
    );
  }
}