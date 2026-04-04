package com.spotwo.spotwo.application.couple.dto;

import java.time.LocalDateTime;

public record InviteResult(
    String inviteCode,
    String inviteUrl,
    LocalDateTime expiredAt
) {}