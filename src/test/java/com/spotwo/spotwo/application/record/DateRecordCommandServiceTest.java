package com.spotwo.spotwo.application.record;

import com.spotwo.spotwo.application.record.dto.CreateRecordCommand;
import com.spotwo.spotwo.application.record.dto.RecordResult;
import com.spotwo.spotwo.application.record.dto.UpdateRecordCommand;
import com.spotwo.spotwo.domain.couple.Couple;
import com.spotwo.spotwo.domain.couple.CoupleRepository;
import com.spotwo.spotwo.domain.record.*;
import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DateRecordCommandServiceTest {

  @InjectMocks
  private DateRecordCommandService commandService;

  @Mock
  private DateRecordRepository dateRecordRepository;

  @Mock
  private CoupleRepository coupleRepository;

  @Mock
  private WeatherTagService weatherTagService;

  private Couple createMatchedCouple() {
    return Couple.reconstruct(
        1L, 1L, 2L, "ABCD123456",
        Couple.CoupleStatus.MATCHED,
        LocalDateTime.now().minusDays(10),
        LocalDateTime.now().minusDays(9),
        LocalDateTime.now().minusDays(10)
    );
  }

  private DateRecord createSavedRecord() {
    return DateRecord.reconstruct(
        1L,           // ← id!
        1L, 1L,
        "JEJU", "제주도",
        33.4996, 126.5312,
        "제주도 여행", "제주도 첫 여행!",
        "https://example.com/photo.jpg",
        LocalDate.of(2026, 10, 15),
        Season.FALL,
        "맑음",
        RecordType.VISIT,
        new ArrayList<>(),
        LocalDateTime.now(),
        LocalDateTime.now()
    );
  }

  @Test
  @DisplayName("기록 작성 성공 - VISIT 타입 날씨 자동태깅")
  void create_success_withWeatherTag() {
    // given
    CreateRecordCommand command = new CreateRecordCommand(
        1L, "JEJU", "제주도",
        33.4996, 126.5312,
        "제주도 여행", "제주도 첫 여행!",
        "https://example.com/photo.jpg",
        LocalDate.of(2026, 10, 15),
        RecordType.VISIT
    );

    given(coupleRepository.findMatchedCoupleByUserId(any()))
        .willReturn(Optional.of(createMatchedCouple()));
    given(weatherTagService.getWeatherTag(any(), any(), any()))
        .willReturn("맑음");
    given(dateRecordRepository.save(any()))
        .willReturn(createSavedRecord());

    // when
    RecordResult result = commandService.create(command);

    // then
    assertThat(result.regionName()).isEqualTo("제주도");
    assertThat(result.season()).isEqualTo("가을");
    assertThat(result.weatherTag()).isEqualTo("맑음");
    assertThat(result.recordType()).isEqualTo("VISIT");

    // 날씨 API 호출됐는지 확인
    then(weatherTagService).should().getWeatherTag(any(), any(), any());
  }

  @Test
  @DisplayName("기록 작성 성공 - WISHLIST 타입은 날씨 태깅 안 함")
  void create_success_wishlist_noWeatherTag() {
    // given
    CreateRecordCommand command = new CreateRecordCommand(
        1L, "GANGNEUNG", "강릉",
        37.7519, 128.8761,
        "강릉 가고싶다", "꼭 가보고 싶어",
        "https://example.com/photo.jpg",
        LocalDate.of(2027, 1, 1),
        RecordType.WISHLIST
    );

    DateRecord wishlistRecord = DateRecord.reconstruct(
        1L,           // ← id!
        1L, 1L,
        "GANGNEUNG", "강릉",
        37.7519, 128.8761,
        "강릉 가고싶다", "꼭 가보고 싶어",
        "https://example.com/photo.jpg",
        LocalDate.of(2027, 1, 1),
        Season.WINTER,   // 1월 → 겨울
        null,            // WISHLIST는 날씨 없음
        RecordType.WISHLIST,
        new ArrayList<>(),
        LocalDateTime.now(),
        LocalDateTime.now()
    );

    given(coupleRepository.findMatchedCoupleByUserId(any()))
        .willReturn(Optional.of(createMatchedCouple()));
    given(dateRecordRepository.save(any())).willReturn(wishlistRecord);

    // when
    commandService.create(command);

    // then - WISHLIST는 날씨 API 호출 안 함!
    then(weatherTagService).should(never())
        .getWeatherTag(any(), any(), any());
  }

  @Test
  @DisplayName("커플 매칭 안 된 유저는 기록 작성 실패")
  void create_fail_coupleNotFound() {
    // given
    CreateRecordCommand command = new CreateRecordCommand(
        1L, "JEJU", "제주도",
        33.4996, 126.5312,
        "제주도 여행", "내용",
        "https://example.com/photo.jpg",
        LocalDate.of(2026, 10, 15),
        RecordType.VISIT
    );

    given(coupleRepository.findMatchedCoupleByUserId(any()))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> commandService.create(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.COUPLE_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("기록 수정 성공")
  void update_success() {
    // given
    UpdateRecordCommand command = new UpdateRecordCommand(
        1L, 1L,
        "수정된 제목", "수정된 내용",
        "https://example.com/new.jpg",
        "JEJU", "제주도",
        33.4996, 126.5312,
        LocalDate.of(2026, 7, 15)
    );

    DateRecord savedRecord = createSavedRecord();

    given(dateRecordRepository.findById(any()))
        .willReturn(Optional.of(savedRecord));
    given(dateRecordRepository.save(any())).willReturn(savedRecord);

    // when
    RecordResult result = commandService.update(command);

    // then
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("타인 기록 수정 실패")
  void update_fail_forbidden() {
    // given
    UpdateRecordCommand command = new UpdateRecordCommand(
        1L, 999L,  // ← 다른 유저 ID
        "수정된 제목", "수정된 내용",
        "https://example.com/new.jpg",
        "JEJU", "제주도",
        33.4996, 126.5312,
        LocalDate.of(2026, 7, 15)
    );

    given(dateRecordRepository.findById(any()))
        .willReturn(Optional.of(createSavedRecord()));

    // when & then
    assertThatThrownBy(() -> commandService.update(command))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.FORBIDDEN.getMessage());
  }

  @Test
  @DisplayName("기록 삭제 성공")
  void delete_success() {
    // given
    given(dateRecordRepository.findById(any()))
        .willReturn(Optional.of(createSavedRecord()));
    willDoNothing().given(dateRecordRepository).deleteById(any());

    // when & then
    assertThatNoException()
        .isThrownBy(() -> commandService.delete(1L, 1L));
  }

  @Test
  @DisplayName("타인 기록 삭제 실패")
  void delete_fail_forbidden() {
    // given
    given(dateRecordRepository.findById(any()))
        .willReturn(Optional.of(createSavedRecord()));

    // when & then
    assertThatThrownBy(() -> commandService.delete(1L, 999L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.FORBIDDEN.getMessage());
  }

  @Test
  @DisplayName("존재하지 않는 기록 삭제 실패")
  void delete_fail_notFound() {
    // given
    given(dateRecordRepository.findById(any()))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> commandService.delete(999L, 1L))
        .isInstanceOf(CustomException.class)
        .hasMessage(ErrorCode.RECORD_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("VISIT → WISHLIST 전환 성공")
  void toggleWishlist_visitToWishlist() {
    // given
    DateRecord recordData = createSavedRecord(); // VISIT 상태

    given(dateRecordRepository.findById(any()))
        .willReturn(Optional.of(recordData));
    given(dateRecordRepository.save(any())).willReturn(recordData);

    // when
    RecordResult result = commandService.toggleWishlist(1L, 1L);

    // then
    assertThat(result.recordType()).isEqualTo("WISHLIST");
  }
}