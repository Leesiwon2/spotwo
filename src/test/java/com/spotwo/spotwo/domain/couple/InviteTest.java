package com.spotwo.spotwo.domain.couple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InviteCodeTest {

  @Test
  @DisplayName("초대 코드 생성 성공")
  void generateInviteCode_success() {
    InviteCode code = InviteCode.generate();

    assertThat(code.value()).isNotNull();
    assertThat(code.value()).hasSize(10);
    assertThat(code.value()).isUpperCase();
  }

  @Test
  @DisplayName("빈 값으로 생성하면 예외 발생")
  void createInviteCode_fail_blank() {
    assertThatThrownBy(() -> new InviteCode(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("초대 코드는 비어있을 수 없습니다.");
  }

  @Test
  @DisplayName("null로 생성하면 예외 발생")
  void createInviteCode_fail_null() {
    assertThatThrownBy(() -> new InviteCode(null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}