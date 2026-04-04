package com.spotwo.spotwo.infrastructure.persistence.couple;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CoupleJpaRepository extends JpaRepository<CoupleJpaEntity, Long> {

  Optional<CoupleJpaEntity> findByInviteCode(String inviteCode);

  @Query("""
        SELECT c FROM CoupleJpaEntity c
        WHERE (c.inviterId = :userId OR c.inviteeId = :userId)
        AND c.status = 'MATCHED'
    """)
  Optional<CoupleJpaEntity> findMatchedCoupleByUserId(@Param("userId") Long userId);

  @Query("""
        SELECT COUNT(c) > 0 FROM CoupleJpaEntity c
        WHERE (c.inviterId = :userId OR c.inviteeId = :userId)
        AND c.status = 'MATCHED'
    """)
  boolean existsMatchedCoupleByUserId(@Param("userId") Long userId);
}