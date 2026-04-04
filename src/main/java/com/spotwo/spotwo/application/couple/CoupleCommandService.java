package com.spotwo.spotwo.application.couple;

import com.spotwo.spotwo.application.couple.dto.AcceptInviteCommand;
import com.spotwo.spotwo.application.couple.dto.CoupleResult;
import com.spotwo.spotwo.application.couple.dto.CreateInviteCommand;
import com.spotwo.spotwo.application.couple.dto.InviteResult;
import com.spotwo.spotwo.domain.couple.Couple;
import com.spotwo.spotwo.domain.couple.CoupleRepository;
import com.spotwo.spotwo.domain.couple.InviteCode;
import com.spotwo.spotwo.domain.user.UserId;
import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CoupleCommandService {

  private final CoupleRepository coupleRepository;

  @Value("${app.base-url}")
  private String baseUrl;

  public InviteResult createInvite(CreateInviteCommand command) {
    UserId inviterId = new UserId(command.inviterId());

    if (coupleRepository.existsMatchedCoupleByUserId(inviterId)) {
      throw new CustomException(ErrorCode.ALREADY_MATCHED);
    }

    Couple couple = Couple.create(inviterId);
    Couple saved = coupleRepository.save(couple);

    String inviteUrl = baseUrl + "/invite?code=" + saved.getInviteCode().value();

    return new InviteResult(
        saved.getInviteCode().value(),
        inviteUrl,
        saved.getInvitedAt().plusHours(48)
    );
  }

  public CoupleResult acceptInvite(AcceptInviteCommand command) {
    UserId inviteeId = new UserId(command.inviteeId());
    InviteCode inviteCode = new InviteCode(command.inviteCode());

    Couple couple = coupleRepository.findByInviteCode(inviteCode)
        .orElseThrow(() -> new CustomException(ErrorCode.INVITE_CODE_NOT_FOUND));

    // 자기 자신 초대 방지
    if (couple.getInviterId().equals(inviteeId)) {
      throw new CustomException(ErrorCode.CANNOT_MATCH_SELF);
    }

    // 만료 확인
    if (couple.isExpired()) {
      throw new CustomException(ErrorCode.INVITE_CODE_EXPIRED);
    }

    // 이미 매칭된 코드 확인
    if (couple.isMatched()) {
      throw new CustomException(ErrorCode.ALREADY_MATCHED);
    }

    // 초대받는 사람 이미 커플인지 확인
    if (coupleRepository.existsMatchedCoupleByUserId(inviteeId)) {
      throw new CustomException(ErrorCode.ALREADY_MATCHED);
    }

    couple.match(inviteeId);
    Couple saved = coupleRepository.save(couple);

    return CoupleResult.from(saved);
  }
}