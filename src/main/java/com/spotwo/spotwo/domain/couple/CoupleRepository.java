package com.spotwo.spotwo.domain.couple;

import com.spotwo.spotwo.domain.user.UserId;

import java.util.Optional;

public interface CoupleRepository {
  Couple save(Couple couple);
  Optional<Couple> findByInviteCode(InviteCode inviteCode);
  Optional<Couple> findMatchedCoupleByUserId(UserId userId);
  boolean existsMatchedCoupleByUserId(UserId userId);
}