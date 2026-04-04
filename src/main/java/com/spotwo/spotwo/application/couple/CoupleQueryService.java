package com.spotwo.spotwo.application.couple;

import com.spotwo.spotwo.application.couple.dto.CoupleResult;
import com.spotwo.spotwo.domain.couple.Couple;
import com.spotwo.spotwo.domain.couple.CoupleRepository;
import com.spotwo.spotwo.domain.user.UserId;
import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoupleQueryService {

  private final CoupleRepository coupleRepository;

  public CoupleResult getMyCouple(Long userId) {
    Couple couple = coupleRepository.findMatchedCoupleByUserId(new UserId(userId))
        .orElseThrow(() -> new CustomException(ErrorCode.COUPLE_NOT_FOUND));
    return CoupleResult.from(couple);
  }
}