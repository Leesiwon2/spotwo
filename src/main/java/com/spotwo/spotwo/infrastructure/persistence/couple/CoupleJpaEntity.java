package com.spotwo.spotwo.infrastructure.persistence.couple;

import com.spotwo.spotwo.domain.couple.Couple;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "couples")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CoupleJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long inviterId;

  private Long inviteeId;

  @Column(unique = true, nullable = false)
  private String inviteCode;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Couple.CoupleStatus status;

  private LocalDateTime invitedAt;
  private LocalDateTime matchedAt;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  // 도메인 → JPA Entity
  public static CoupleJpaEntity fromDomain(Couple couple) {
    CoupleJpaEntity entity = new CoupleJpaEntity();
    if (couple.getId() != null) entity.id = couple.getId().value();
    entity.inviterId = couple.getInviterId().value();
    entity.inviteeId = couple.getInviteeId() != null
        ? couple.getInviteeId().value() : null;
    entity.inviteCode = couple.getInviteCode().value();
    entity.status = couple.getStatus();
    entity.invitedAt = couple.getInvitedAt();
    entity.matchedAt = couple.getMatchedAt();
    return entity;
  }

  // JPA Entity → 도메인
  public Couple toDomain() {
    return Couple.reconstruct(
        id, inviterId, inviteeId, inviteCode,
        status, invitedAt, matchedAt, createdAt
    );
  }
}