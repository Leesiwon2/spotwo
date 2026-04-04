package com.spotwo.spotwo.application.couple;

import com.spotwo.spotwo.application.couple.dto.AcceptInviteCommand;
import com.spotwo.spotwo.application.couple.dto.CoupleResult;
import com.spotwo.spotwo.application.couple.dto.CreateInviteCommand;
import com.spotwo.spotwo.application.couple.dto.InviteResult;
import com.spotwo.spotwo.domain.couple.Couple;
import com.spotwo.spotwo.domain.couple.CoupleRepository;
import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CoupleCommandServiceTest {

  @InjectMocks
  private CoupleCommandService coupleCommandService;

  @Mock
  private CoupleRepository coupleRepository;

  @Test
  @DisplayName("초대 링크 생성 성공")
  void createInvite_success() {
    // given
    ReflectionTestUtils.setField(
        coupleCommandService, "baseUrl", "http://localhost:3000");

    CreateInviteCommand command = new CreateInviteCommand(1L);

    Couple savedCouple = Couple.reconstruct(
        1L, 1L, null, "ABCD123456",
        Couple.CoupleStatus.PENDING,
        LocalDateTime.now(), null, LocalDateTime.now()
    );

    given(coupleRepository.existsMatchedCoupleByUserId(any())).willReturn(false);
    given(coupleRepository.save(any())).willReturn(savedCouple);

    // when
    InviteResult result = coupleCommandService.createInvite(command);

    // then
    assertThat(result.inviteCode()).isEqualTo("ABCD123456");
    assertThat(result.inviteUrl()).contains("ABCD123456");
  }

  @Test
  @DisplayName("이미 매칭된 유저는 초대 링크 생성 실패")
  void createInvite_fail_alreadyMatched() {
    // given
    CreateInviteCommand command = new CreateInviteCommand(1L);
    given(coupleRepository.existsMatchedCoupleByUserId(any())).willReturn(true);

    // when & then
    assertThatThrownBy(() -> coupleCommandService.createInvite(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.ALREADY_MATCHED.getMessage());
  }

  @Test
  @DisplayName("초대 수락 성공")
  void acceptInvite_success() {
    // given
    AcceptInviteCommand command = new AcceptInviteCommand("ABCD123456", 2L);

    Couple couple = Couple.reconstruct(
        1L, 1L, null, "ABCD123456",
        Couple.CoupleStatus.PENDING,
        LocalDateTime.now(), null, LocalDateTime.now()
    );

    Couple matchedCouple = Couple.reconstruct(
        1L, 1L, 2L, "ABCD123456",
        Couple.CoupleStatus.MATCHED,
        LocalDateTime.now().minusMinutes(5),
        LocalDateTime.now(), LocalDateTime.now().minusMinutes(5)
    );

    given(coupleRepository.findByInviteCode(any())).willReturn(Optional.of(couple));
    given(coupleRepository.existsMatchedCoupleByUserId(any())).willReturn(false);
    given(coupleRepository.save(any())).willReturn(matchedCouple);

    // when
    CoupleResult result = coupleCommandService.acceptInvite(command);

    // then
    assertThat(result.status()).isEqualTo("MATCHED");
    assertThat(result.inviterId()).isEqualTo(1L);
    assertThat(result.inviteeId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("자기 자신 초대 수락 실패")
  void acceptInvite_fail_matchSelf() {
    // given
    AcceptInviteCommand command = new AcceptInviteCommand("ABCD123456", 1L);

    Couple couple = Couple.reconstruct(
        1L, 1L, null, "ABCD123456",
        Couple.CoupleStatus.PENDING,
        LocalDateTime.now(), null, LocalDateTime.now()
    );

    given(coupleRepository.findByInviteCode(any())).willReturn(Optional.of(couple));

    // when & then
    assertThatThrownBy(() -> coupleCommandService.acceptInvite(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.CANNOT_MATCH_SELF.getMessage());
  }

  @Test
  @DisplayName("만료된 초대 코드 수락 실패")
  void acceptInvite_fail_expired() {
    // given
    AcceptInviteCommand command = new AcceptInviteCommand("ABCD123456", 2L);

    Couple expiredCouple = Couple.reconstruct(
        1L, 1L, null, "ABCD123456",
        Couple.CoupleStatus.PENDING,
        LocalDateTime.now().minusHours(49), // 만료!
        null, LocalDateTime.now().minusHours(49)
    );

    given(coupleRepository.findByInviteCode(any()))
        .willReturn(Optional.of(expiredCouple));

    // when & then
    assertThatThrownBy(() -> coupleCommandService.acceptInvite(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVITE_CODE_EXPIRED.getMessage());
  }

  @Test
  @DisplayName("없는 초대 코드 수락 실패")
  void acceptInvite_fail_notFound() {
    // given
    AcceptInviteCommand command = new AcceptInviteCommand("INVALID123", 2L);
    given(coupleRepository.findByInviteCode(any())).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> coupleCommandService.acceptInvite(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVITE_CODE_NOT_FOUND.getMessage());
  }
}