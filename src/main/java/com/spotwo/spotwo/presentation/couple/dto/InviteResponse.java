package com.spotwo.spotwo.presentation.couple.dto;

import com.spotwo.spotwo.application.couple.dto.InviteResult;

import java.time.LocalDateTime;

public record InviteResponse(
    String inviteCode,
    String inviteUrl,
    LocalDateTime expiredAt
) {
  public static InviteResponse from(InviteResult result) {
    return new InviteResponse(
        result.inviteCode(),
        result.inviteUrl(),
        result.expiredAt()
    );
  }
}