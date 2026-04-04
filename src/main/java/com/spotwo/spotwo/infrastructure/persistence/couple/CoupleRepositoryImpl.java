package com.spotwo.spotwo.infrastructure.persistence.couple;

import com.spotwo.spotwo.domain.couple.Couple;
import com.spotwo.spotwo.domain.couple.CoupleRepository;
import com.spotwo.spotwo.domain.couple.InviteCode;
import com.spotwo.spotwo.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CoupleRepositoryImpl implements CoupleRepository {

  private final CoupleJpaRepository coupleJpaRepository;

  @Override
  public Couple save(Couple couple) {
    CoupleJpaEntity entity = CoupleJpaEntity.fromDomain(couple);
    return coupleJpaRepository.save(entity).toDomain();
  }

  @Override
  public Optional<Couple> findByInviteCode(InviteCode inviteCode) {
    return coupleJpaRepository.findByInviteCode(inviteCode.value())
        .map(CoupleJpaEntity::toDomain);
  }

  @Override
  public Optional<Couple> findMatchedCoupleByUserId(UserId userId) {
    return coupleJpaRepository.findMatchedCoupleByUserId(userId.value())
        .map(CoupleJpaEntity::toDomain);
  }

  @Override
  public boolean existsMatchedCoupleByUserId(UserId userId) {
    return coupleJpaRepository.existsMatchedCoupleByUserId(userId.value());
  }
}