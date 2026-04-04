package com.spotwo.spotwo.domain.couple;

import java.util.UUID;

public record InviteCode(String value) {
  public InviteCode {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("초대 코드는 비어있을 수 없습니다.");
    }
  }

  public static InviteCode generate() {
    return new InviteCode(
        UUID.randomUUID().toString()
            .replace("-", "")
            .substring(0, 10)
            .toUpperCase()
    );
  }
}