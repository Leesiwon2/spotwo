package com.spotwo.spotwo.domain.user;

import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;

public record Email(String value) {
  public Email {
    if (value == null || !value.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
      throw new CustomException(ErrorCode.INVALID_EMAIL);
    }
  }
}