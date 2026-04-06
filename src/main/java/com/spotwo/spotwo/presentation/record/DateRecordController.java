package com.spotwo.spotwo.presentation.record;

import com.spotwo.spotwo.application.record.DateRecordCommandService;
import com.spotwo.spotwo.application.record.DateRecordQueryService;
import com.spotwo.spotwo.application.record.dto.*;
import com.spotwo.spotwo.domain.user.User;
import com.spotwo.spotwo.global.response.ApiResponse;
import com.spotwo.spotwo.presentation.record.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class DateRecordController {

  private final DateRecordCommandService commandService;
  private final DateRecordQueryService queryService;

  // 지도 핀 전체 조회 (메인 화면)
  @GetMapping("/map")
  public ResponseEntity<ApiResponse<List<MapPinResponse>>> getMapPins(
      @AuthenticationPrincipal User user) {
    List<MapPinResponse> pins = queryService.getMapPins(user.getId().value())
        .stream()
        .map(MapPinResponse::from)
        .toList();
    return ResponseEntity.ok(ApiResponse.ok(pins));
  }

  // 기록 작성
  @PostMapping
  public ResponseEntity<ApiResponse<RecordResponse>> create(
      @Valid @RequestBody CreateRecordRequest request,
      @AuthenticationPrincipal User user) {

    RecordResult result = commandService.create(new CreateRecordCommand(
        user.getId().value(),
        request.regionCode(),
        request.regionName(),
        request.latitude(),
        request.longitude(),
        request.title(),
        request.content(),
        request.thumbnailUrl(),
        request.visitDate(),
        request.recordType()
    ));
    return ResponseEntity.ok(ApiResponse.ok(RecordResponse.from(result)));
  }

  // 기록 상세 조회
  @GetMapping("/{recordId}")
  public ResponseEntity<ApiResponse<RecordResponse>> getRecord(
      @PathVariable Long recordId,
      @AuthenticationPrincipal User user) {
    RecordResult result = queryService.getRecord(recordId, user.getId().value());
    return ResponseEntity.ok(ApiResponse.ok(RecordResponse.from(result)));
  }

  // 기록 수정
  @PutMapping("/{recordId}")
  public ResponseEntity<ApiResponse<RecordResponse>> update(
      @PathVariable Long recordId,
      @Valid @RequestBody UpdateRecordRequest request,
      @AuthenticationPrincipal User user) {
    RecordResult result = commandService.update(new UpdateRecordCommand(
        recordId, user.getId().value(),
        request.title(), request.content(), request.thumbnailUrl(),
        request.regionCode(), request.regionName(),
        request.latitude(), request.longitude(), request.visitDate()
    ));
    return ResponseEntity.ok(ApiResponse.ok(RecordResponse.from(result)));
  }

  // 기록 삭제
  @DeleteMapping("/{recordId}")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long recordId,
      @AuthenticationPrincipal User user) {
    commandService.delete(recordId, user.getId().value());
    return ResponseEntity.ok(ApiResponse.ok(null));
  }

  // 위시리스트 전환
  @PatchMapping("/{recordId}/toggle")
  public ResponseEntity<ApiResponse<RecordResponse>> toggle(
      @PathVariable Long recordId,
      @AuthenticationPrincipal User user) {
    RecordResult result = commandService.toggleWishlist(
        recordId, user.getId().value());
    return ResponseEntity.ok(ApiResponse.ok(RecordResponse.from(result)));
  }

  // 통계 조회
  @GetMapping("/stats")
  public ResponseEntity<ApiResponse<RecordStatsResult>> getStats(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(
        ApiResponse.ok(queryService.getStats(user.getId().value())));
  }
}