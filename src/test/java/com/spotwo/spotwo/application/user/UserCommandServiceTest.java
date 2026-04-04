package com.spotwo.spotwo.application.user;

import com.spotwo.spotwo.application.user.dto.LoginCommand;
import com.spotwo.spotwo.application.user.dto.SignupCommand;
import com.spotwo.spotwo.domain.user.User;
import com.spotwo.spotwo.domain.user.UserRepository;
import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;
import com.spotwo.spotwo.global.jwt.JwtToken;
import com.spotwo.spotwo.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

  @InjectMocks
  private UserCommandService userCommandService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("회원가입 성공")
  void signup_success() {
    // given
    SignupCommand command = new SignupCommand(
        "test@spotwo.com", "test1234!", "민준");

    User savedUser = User.reconstruct(
        1L, "test@spotwo.com", "encodedPassword",
        "민준", null, User.AuthProvider.LOCAL,
        null, User.Role.USER, null, null);

    given(userRepository.existsByEmail(any())).willReturn(false);
    given(passwordEncoder.encode(any())).willReturn("encodedPassword");
    given(userRepository.save(any())).willReturn(savedUser);
    given(jwtTokenProvider.generateToken(any())).willReturn(
        JwtToken.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .userId(1L)
            .nickname("민준")
            .build()
    );

    // when
    JwtToken token = userCommandService.signup(command);

    // then
    assertThat(token.getAccessToken()).isEqualTo("accessToken");
    assertThat(token.getNickname()).isEqualTo("민준");
    assertThat(token.getUserId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("중복 이메일 회원가입 실패")
  void signup_fail_duplicateEmail() {
    // given
    SignupCommand command = new SignupCommand(
        "test@spotwo.com", "test1234!", "민준");

    given(userRepository.existsByEmail(any())).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userCommandService.signup(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
  }

  @Test
  @DisplayName("로그인 성공")
  void login_success() {
    // given
    LoginCommand command = new LoginCommand(
        "test@spotwo.com", "test1234!");

    User user = User.reconstruct(
        1L, "test@spotwo.com", "encodedPassword",
        "민준", null, User.AuthProvider.LOCAL,
        null, User.Role.USER, null, null);

    given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
    given(passwordEncoder.matches(any(), any())).willReturn(true);
    given(jwtTokenProvider.generateToken(any())).willReturn(
        JwtToken.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .userId(1L)
            .nickname("민준")
            .build()
    );

    // when
    JwtToken token = userCommandService.login(command);

    // then
    assertThat(token.getAccessToken()).isEqualTo("accessToken");
  }

  @Test
  @DisplayName("존재하지 않는 유저 로그인 실패")
  void login_fail_userNotFound() {
    // given
    LoginCommand command = new LoginCommand(
        "notexist@spotwo.com", "test1234!");

    given(userRepository.findByEmail(any())).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userCommandService.login(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("틀린 비밀번호 로그인 실패")
  void login_fail_invalidPassword() {
    // given
    LoginCommand command = new LoginCommand(
        "test@spotwo.com", "wrongpassword");

    User user = User.reconstruct(
        1L, "test@spotwo.com", "encodedPassword",
        "민준", null, User.AuthProvider.LOCAL,
        null, User.Role.USER, null, null);

    given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
    given(passwordEncoder.matches(any(), any())).willReturn(false);

    // when & then
    assertThatThrownBy(() -> userCommandService.login(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());
  }
}