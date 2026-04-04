package com.spotwo.spotwo.domain.couple;

public record CoupleId(Long value) {
  public CoupleId {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException("CoupleId는 양수여야 합니다.");
    }
  }
}