package com.spotwo.spotwo.domain.user;

public record UserId(Long value) {
  public UserId {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException("UserId는 양수여야 합니다.");
    }
  }
}