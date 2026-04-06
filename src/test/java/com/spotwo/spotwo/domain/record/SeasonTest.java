package com.spotwo.spotwo.domain.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class SeasonTest {

  @Test
  @DisplayName("3월은 봄")
  void march_is_spring() {
    assertThat(Season.from(LocalDate.of(2026, 3, 15)))
        .isEqualTo(Season.SPRING);
  }

  @Test
  @DisplayName("6월은 여름")
  void june_is_summer() {
    assertThat(Season.from(LocalDate.of(2026, 6, 15)))
        .isEqualTo(Season.SUMMER);
  }

  @Test
  @DisplayName("10월은 가을")
  void october_is_fall() {
    assertThat(Season.from(LocalDate.of(2026, 10, 15)))
        .isEqualTo(Season.FALL);
  }

  @Test
  @DisplayName("12월은 겨울")
  void december_is_winter() {
    assertThat(Season.from(LocalDate.of(2026, 12, 15)))
        .isEqualTo(Season.WINTER);
  }

  @Test
  @DisplayName("계절 한국어 변환")
  void toKorean() {
    assertThat(Season.SPRING.toKorean()).isEqualTo("봄");
    assertThat(Season.SUMMER.toKorean()).isEqualTo("여름");
    assertThat(Season.FALL.toKorean()).isEqualTo("가을");
    assertThat(Season.WINTER.toKorean()).isEqualTo("겨울");
  }
}