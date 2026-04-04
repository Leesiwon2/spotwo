package com.spotwo.spotwo.global.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtToken {
  private String accessToken;
  private String refreshToken;
  private Long userId;
  private String nickname;
}