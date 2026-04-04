package com.spotwo.spotwo.domain.user;

import java.time.LocalDateTime;

public class User {

  private final UserId id;
  private Email email;
  private String password;
  private String nickname;
  private String profileImageUrl;
  private final AuthProvider provider;
  private final String providerId;
  private final Role role;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // 일반 회원가입
  public static User ofLocal(String email, String encodedPassword, String nickname) {
    return new User(
        null,
        new Email(email),
        encodedPassword,
        nickname,
        null,
        AuthProvider.LOCAL,
        null,
        Role.USER,
        LocalDateTime.now(),
        LocalDateTime.now()
    );
  }

  // 카카오 로그인
  public static User ofKakao(String email, String nickname,
      String profileImageUrl, String providerId) {
    return new User(
        null,
        email != null ? new Email(email) : null,
        null,
        nickname,
        profileImageUrl,
        AuthProvider.KAKAO,
        providerId,
        Role.USER,
        LocalDateTime.now(),
        LocalDateTime.now()
    );
  }

  // 재구성용 (DB 조회)
  public static User reconstruct(Long id, String email, String password,
      String nickname, String profileImageUrl,
      AuthProvider provider, String providerId,
      Role role, LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    return new User(
        new UserId(id),
        email != null ? new Email(email) : null,
        password, nickname, profileImageUrl,
        provider, providerId, role, createdAt, updatedAt
    );
  }

  private User(UserId id, Email email, String password, String nickname,
      String profileImageUrl, AuthProvider provider, String providerId,
      Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.provider = provider;
    this.providerId = providerId;
    this.role = role;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public void updateProfile(String nickname, String profileImageUrl) {
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
    this.updatedAt = LocalDateTime.now();
  }

  // Getters
  public UserId getId() { return id; }
  public Email getEmail() { return email; }
  public String getEmailValue() { return email != null ? email.value() : null; }
  public String getPassword() { return password; }
  public String getNickname() { return nickname; }
  public String getProfileImageUrl() { return profileImageUrl; }
  public AuthProvider getProvider() { return provider; }
  public String getProviderId() { return providerId; }
  public Role getRole() { return role; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }

  public enum AuthProvider { LOCAL, KAKAO }
  public enum Role { USER, ADMIN }
}