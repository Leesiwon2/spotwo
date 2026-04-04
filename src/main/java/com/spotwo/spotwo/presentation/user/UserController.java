package com.spotwo.spotwo.presentation.user;

import com.spotwo.spotwo.application.user.UserCommandService;
import com.spotwo.spotwo.application.user.UserQueryService;
import com.spotwo.spotwo.application.user.dto.LoginCommand;
import com.spotwo.spotwo.application.user.dto.SignupCommand;
import com.spotwo.spotwo.domain.user.User;
import com.spotwo.spotwo.global.jwt.JwtToken;
import com.spotwo.spotwo.global.response.ApiResponse;
import com.spotwo.spotwo.presentation.user.dto.LoginRequest;
import com.spotwo.spotwo.presentation.user.dto.SignupRequest;
import com.spotwo.spotwo.presentation.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

  private final UserCommandService userCommandService;
  private final UserQueryService userQueryService;

  @PostMapping("/auth/signup")
  public ResponseEntity<ApiResponse<JwtToken>> signup(
      @Valid @RequestBody SignupRequest request) {
    JwtToken token = userCommandService.signup(
        new SignupCommand(request.email(), request.password(), request.nickname()));
    return ResponseEntity.ok(ApiResponse.ok(token));
  }

  @PostMapping("/auth/login")
  public ResponseEntity<ApiResponse<JwtToken>> login(
      @Valid @RequestBody LoginRequest request) {
    JwtToken token = userCommandService.login(
        new LoginCommand(request.email(), request.password()));
    return ResponseEntity.ok(ApiResponse.ok(token));
  }

  @GetMapping("/users/me")
  public ResponseEntity<ApiResponse<UserResponse>> getProfile(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(ApiResponse.ok(
        UserResponse.from(userQueryService.getProfile(user.getId().value()))));
  }
}