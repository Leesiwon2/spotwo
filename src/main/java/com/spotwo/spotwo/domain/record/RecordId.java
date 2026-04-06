package com.spotwo.spotwo.domain.record;

public record RecordId(Long value) {
  public RecordId {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException("RecordId는 양수여야 합니다.");
    }
  }
}