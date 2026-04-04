package com.spotwo.spotwo.presentation.couple;

import com.spotwo.spotwo.application.couple.CoupleCommandService;
import com.spotwo.spotwo.application.couple.CoupleQueryService;
import com.spotwo.spotwo.application.couple.dto.AcceptInviteCommand;
import com.spotwo.spotwo.application.couple.dto.CreateInviteCommand;
import com.spotwo.spotwo.domain.user.User;
import com.spotwo.spotwo.global.response.ApiResponse;
import com.spotwo.spotwo.presentation.couple.dto.CoupleResponse;
import com.spotwo.spotwo.presentation.couple.dto.InviteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/couples")
@RequiredArgsConstructor
public class CoupleController {

  private final CoupleCommandService coupleCommandService;
  private final CoupleQueryService coupleQueryService;

  @PostMapping("/invite")
  public ResponseEntity<ApiResponse<InviteResponse>> createInvite(
      @AuthenticationPrincipal User user) {
    InviteResponse response = InviteResponse.from(
        coupleCommandService.createInvite(
            new CreateInviteCommand(user.getId().value())));
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  @PostMapping("/invite/accept/{inviteCode}")
  public ResponseEntity<ApiResponse<CoupleResponse>> acceptInvite(
      @PathVariable String inviteCode,
      @AuthenticationPrincipal User user) {
    CoupleResponse response = CoupleResponse.from(
        coupleCommandService.acceptInvite(
            new AcceptInviteCommand(inviteCode, user.getId().value())));
    return ResponseEntity.ok(ApiResponse.ok("커플 매칭 완료! 🎉", response));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<CoupleResponse>> getMyCouple(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(ApiResponse.ok(
        CoupleResponse.from(
            coupleQueryService.getMyCouple(user.getId().value()))));
  }
}