package com.spotwo.spotwo.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN(401, "만료된 토큰입니다."),
  UNAUTHORIZED(401, "인증이 필요합니다."),

  USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
  EMAIL_ALREADY_EXISTS(409, "이미 사용중인 이메일입니다."),
  INVALID_PASSWORD(400, "비밀번호가 올바르지 않습니다."),

  INVITE_CODE_NOT_FOUND(404, "초대 코드를 찾을 수 없습니다."),
  INVITE_CODE_EXPIRED(400, "만료된 초대 코드입니다. (48시간 초과)"),
  ALREADY_MATCHED(400, "이미 커플 매칭이 완료되었습니다."),
  CANNOT_MATCH_SELF(400, "자기 자신과 매칭할 수 없습니다."),
  COUPLE_NOT_FOUND(404, "커플 정보를 찾을 수 없습니다.");

  private final int status;
  private final String message;
}