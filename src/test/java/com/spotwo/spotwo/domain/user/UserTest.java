package com.spotwo.spotwo.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

  @Test
  @DisplayName("일반 회원가입 유저 생성 성공")
  void createLocalUser_success() {
    User user = User.ofLocal("test@spotwo.com", "encodedPassword", "민준");

    assertThat(user.getEmailValue()).isEqualTo("test@spotwo.com");
    assertThat(user.getNickname()).isEqualTo("민준");
    assertThat(user.getProvider()).isEqualTo(User.AuthProvider.LOCAL);
    assertThat(user.getRole()).isEqualTo(User.Role.USER);
    assertThat(user.getPassword()).isEqualTo("encodedPassword");
  }

  @Test
  @DisplayName("카카오 로그인 유저 생성 성공")
  void createKakaoUser_success() {
    User user = User.ofKakao(null, "민준", "http://image.url", "kakao123");

    assertThat(user.getNickname()).isEqualTo("민준");
    assertThat(user.getProvider()).isEqualTo(User.AuthProvider.KAKAO);
    assertThat(user.getProviderId()).isEqualTo("kakao123");
    assertThat(user.getEmailValue()).isNull(); // 이메일 없어도 됨
  }

  @Test
  @DisplayName("프로필 업데이트 성공")
  void updateProfile_success() {
    User user = User.ofLocal("test@spotwo.com", "password", "민준");
    user.updateProfile("이시원", "http://new-image.url");

    assertThat(user.getNickname()).isEqualTo("이시원");
    assertThat(user.getProfileImageUrl()).isEqualTo("http://new-image.url");
  }
}