package com.spotwo.spotwo.global.jwt;

import com.spotwo.spotwo.domain.user.User;
import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long expiration;

  @Value("${jwt.refresh-expiration}")
  private long refreshExpiration;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public JwtToken generateToken(User user) {
    String accessToken = buildToken(user.getId().value().toString(), expiration);
    String refreshToken = buildToken(user.getId().value().toString(), refreshExpiration);

    return JwtToken.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .userId(user.getId().value())
        .nickname(user.getNickname())
        .build();
  }

  private String buildToken(String subject, long expiry) {
    return Jwts.builder()
        .subject(subject)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiry))
        .signWith(getSigningKey())
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      throw new CustomException(ErrorCode.EXPIRED_TOKEN);
    } catch (Exception e) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }

  public Long getUserId(String token) {
    return Long.parseLong(
        Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject()
    );
  }
}