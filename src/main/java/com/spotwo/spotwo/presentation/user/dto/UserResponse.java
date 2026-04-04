package com.spotwo.spotwo.presentation.user.dto;

import com.spotwo.spotwo.application.user.dto.UserResult;

public record UserResponse(
    Long id,
    String email,
    String nickname,
    String profileImageUrl,
    String provider
) {
  public static UserResponse from(UserResult result) {
    return new UserResponse(
        result.id(),
        result.email(),
        result.nickname(),
        result.profileImageUrl(),
        result.provider()
    );
  }
}