package com.spotwo.spotwo.domain.user;

public record Email(String value) {
  public Email {
    if (value == null || !value.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
      throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
    }
  }
}