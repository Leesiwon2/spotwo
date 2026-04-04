package com.spotwo.spotwo.application.user.dto;

import com.spotwo.spotwo.domain.user.User;

public record UserResult(
    Long id,
    String email,
    String nickname,
    String profileImageUrl,
    String provider
) {
  public static UserResult from(User user) {
    return new UserResult(
        user.getId().value(),
        user.getEmailValue(),
        user.getNickname(),
        user.getProfileImageUrl(),
        user.getProvider().name()
    );
  }
}