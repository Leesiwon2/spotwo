package com.spotwo.spotwo.domain.couple;

import com.spotwo.spotwo.domain.user.UserId;

import java.time.LocalDateTime;

public class Couple {

  private final CoupleId id;
  private final UserId inviterId;
  private UserId inviteeId;
  private final InviteCode inviteCode;
  private CoupleStatus status;
  private final LocalDateTime invitedAt;
  private LocalDateTime matchedAt;
  private final LocalDateTime createdAt;

  public static Couple create(UserId inviterId) {
    return new Couple(
        null,
        inviterId,
        null,
        InviteCode.generate(),
        CoupleStatus.PENDING,
        LocalDateTime.now(),
        null,
        LocalDateTime.now()
    );
  }

  // 재구성용 (DB 조회)
  public static Couple reconstruct(Long id, Long inviterId, Long inviteeId,
      String inviteCode, CoupleStatus status,
      LocalDateTime invitedAt, LocalDateTime matchedAt,
      LocalDateTime createdAt) {
    return new Couple(
        new CoupleId(id),
        new UserId(inviterId),
        inviteeId != null ? new UserId(inviteeId) : null,
        new InviteCode(inviteCode),
        status,
        invitedAt,
        matchedAt,
        createdAt
    );
  }

  private Couple(CoupleId id, UserId inviterId, UserId inviteeId,
      InviteCode inviteCode, CoupleStatus status,
      LocalDateTime invitedAt, LocalDateTime matchedAt,
      LocalDateTime createdAt) {
    this.id = id;
    this.inviterId = inviterId;
    this.inviteeId = inviteeId;
    this.inviteCode = inviteCode;
    this.status = status;
    this.invitedAt = invitedAt;
    this.matchedAt = matchedAt;
    this.createdAt = createdAt;
  }

  // 비즈니스 메서드
  public void match(UserId inviteeId) {
    this.inviteeId = inviteeId;
    this.status = CoupleStatus.MATCHED;
    this.matchedAt = LocalDateTime.now();
  }

  public boolean isExpired() {
    return invitedAt.plusHours(48).isBefore(LocalDateTime.now());
  }

  public boolean isPending() {
    return this.status == CoupleStatus.PENDING;
  }

  public boolean isMatched() {
    return this.status == CoupleStatus.MATCHED;
  }

  // Getters
  public CoupleId getId() { return id; }
  public UserId getInviterId() { return inviterId; }
  public UserId getInviteeId() { return inviteeId; }
  public InviteCode getInviteCode() { return inviteCode; }
  public CoupleStatus getStatus() { return status; }
  public LocalDateTime getInvitedAt() { return invitedAt; }
  public LocalDateTime getMatchedAt() { return matchedAt; }
  public LocalDateTime getCreatedAt() { return createdAt; }

  public enum CoupleStatus { PENDING, MATCHED, BROKEN }
}