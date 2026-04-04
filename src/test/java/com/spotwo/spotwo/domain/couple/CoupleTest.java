package com.spotwo.spotwo.domain.couple;

import com.spotwo.spotwo.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class CoupleTest {

  @Test
  @DisplayName("커플 생성 성공 - 초기 상태는 PENDING")
  void createCouple_success() {
    UserId inviterId = new UserId(1L);
    Couple couple = Couple.create(inviterId);

    assertThat(couple.getInviterId()).isEqualTo(inviterId);
    assertThat(couple.getStatus()).isEqualTo(Couple.CoupleStatus.PENDING);
    assertThat(couple.getInviteeId()).isNull();
    assertThat(couple.getInviteCode()).isNotNull();
  }

  @Test
  @DisplayName("커플 매칭 성공")
  void matchCouple_success() {
    UserId inviterId = new UserId(1L);
    UserId inviteeId = new UserId(2L);
    Couple couple = Couple.create(inviterId);

    couple.match(inviteeId);

    assertThat(couple.getStatus()).isEqualTo(Couple.CoupleStatus.MATCHED);
    assertThat(couple.getInviteeId()).isEqualTo(inviteeId);
    assertThat(couple.getMatchedAt()).isNotNull();
  }

  @Test
  @DisplayName("48시간 이내 초대 코드는 만료되지 않음")
  void isExpired_false() {
    UserId inviterId = new UserId(1L);
    Couple couple = Couple.create(inviterId);

    assertThat(couple.isExpired()).isFalse();
  }

  @Test
  @DisplayName("48시간 이후 초대 코드는 만료됨")
  void isExpired_true() {
    Couple couple = Couple.reconstruct(
        1L, 1L, null, "ABCD123456",
        Couple.CoupleStatus.PENDING,
        LocalDateTime.now().minusHours(49), // 49시간 전
        null,
        LocalDateTime.now().minusHours(49)
    );

    assertThat(couple.isExpired()).isTrue();
  }

  @Test
  @DisplayName("PENDING 상태 확인")
  void isPending_true() {
    Couple couple = Couple.create(new UserId(1L));
    assertThat(couple.isPending()).isTrue();
    assertThat(couple.isMatched()).isFalse();
  }
}