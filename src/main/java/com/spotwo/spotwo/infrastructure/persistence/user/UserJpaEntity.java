package com.spotwo.spotwo.infrastructure.persistence.user;

import com.spotwo.spotwo.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  private String password;

  @Column(nullable = false)
  private String nickname;

  private String profileImageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private User.AuthProvider provider;

  private String providerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private User.Role role;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  // 도메인 → JPA Entity
  public static UserJpaEntity fromDomain(User user) {
    UserJpaEntity entity = new UserJpaEntity();
    entity.email = user.getEmailValue();
    entity.password = user.getPassword();
    entity.nickname = user.getNickname();
    entity.profileImageUrl = user.getProfileImageUrl();
    entity.provider = user.getProvider();
    entity.providerId = user.getProviderId();
    entity.role = user.getRole();
    return entity;
  }

  // JPA Entity → 도메인
  public User toDomain() {
    return User.reconstruct(
        id, email, password, nickname, profileImageUrl,
        provider, providerId, role, createdAt, updatedAt
    );
  }
}