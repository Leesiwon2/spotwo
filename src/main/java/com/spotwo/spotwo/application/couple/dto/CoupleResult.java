package com.spotwo.spotwo.application.couple.dto;

import com.spotwo.spotwo.domain.couple.Couple;

import java.time.LocalDateTime;

public record CoupleResult(
    Long coupleId,
    Long inviterId,
    Long inviteeId,
    String status,
    LocalDateTime matchedAt
) {
  public static CoupleResult from(Couple couple) {
    return new CoupleResult(
        couple.getId().value(),
        couple.getInviterId().value(),
        couple.getInviteeId() != null ? couple.getInviteeId().value() : null,
        couple.getStatus().name(),
        couple.getMatchedAt()
    );
  }
}