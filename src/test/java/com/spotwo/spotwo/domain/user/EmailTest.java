package com.spotwo.spotwo.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

  @Test
  @DisplayName("올바른 이메일 형식이면 생성 성공")
  void createEmail_success() {
    assertThatNoException()
        .isThrownBy(() -> new Email("test@spotwo.com"));
  }

  @Test
  @DisplayName("잘못된 이메일 형식이면 예외 발생")
  void createEmail_fail_invalidFormat() {
    assertThatThrownBy(() -> new Email("invalid-email"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이메일 형식이 올바르지 않습니다.");
  }

  @Test
  @DisplayName("null이면 예외 발생")
  void createEmail_fail_null() {
    assertThatThrownBy(() -> new Email(null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}