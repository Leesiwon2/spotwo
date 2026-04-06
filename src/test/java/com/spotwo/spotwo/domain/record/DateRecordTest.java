package com.spotwo.spotwo.domain.record;

import com.spotwo.spotwo.domain.couple.CoupleId;
import com.spotwo.spotwo.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class DateRecordTest {

  private DateRecord createSampleRecord() {
    return DateRecord.create(
        new CoupleId(1L),
        new UserId(1L),
        "JEJU", "제주도",
        33.4996, 126.5312,
        "제주도 여행",
        "제주도 첫 여행!",
        "https://example.com/photo.jpg",
        LocalDate.of(2026, 10, 15),
        RecordType.VISIT
    );
  }

  @Test
  @DisplayName("기록 생성 시 계절 자동 태깅")
  void create_autoTagSeason() {
    DateRecord recordData = createSampleRecord();

    assertThat(recordData.getSeason()).isEqualTo(Season.FALL);  // 10월 → 가을
  }

  @Test
  @DisplayName("기록 생성 시 recordType 설정")
  void create_recordType() {
    DateRecord recordData = createSampleRecord();

    assertThat(recordData.getRecordType()).isEqualTo(RecordType.VISIT);
  }

  @Test
  @DisplayName("기록 수정 시 계절 재계산")
  void update_recalculateSeason() {
    DateRecord recordData = createSampleRecord();

    // 10월 → 가을
    assertThat(recordData.getSeason()).isEqualTo(Season.FALL);

    // 7월로 수정 → 여름으로 변경
    recordData.update(
        "수정된 제목", "수정된 내용",
        "https://example.com/new.jpg",
        "JEJU", "제주도",
        33.4996, 126.5312,
        LocalDate.of(2026, 7, 15)
    );

    assertThat(recordData.getSeason()).isEqualTo(Season.SUMMER);
  }

  @Test
  @DisplayName("VISIT → WISHLIST 전환")
  void toggleToWishlist() {
    DateRecord recordData = createSampleRecord();

    assertThat(recordData.getRecordType()).isEqualTo(RecordType.VISIT);

    recordData.toggleToWishlist();

    assertThat(recordData.getRecordType()).isEqualTo(RecordType.WISHLIST);
  }

  @Test
  @DisplayName("WISHLIST → VISIT 전환")
  void toggleToVisit() {
    DateRecord recordData = DateRecord.create(
        new CoupleId(1L), new UserId(1L),
        "JEJU", "제주도",
        33.4996, 126.5312,
        "강릉 가고싶다", "꼭 가보고 싶어",
        "https://example.com/photo.jpg",
        LocalDate.of(2027, 1, 1),
        RecordType.WISHLIST
    );

    recordData.toggleToVisit();

    assertThat(recordData.getRecordType()).isEqualTo(RecordType.VISIT);
  }

  @Test
  @DisplayName("날씨 태그 설정")
  void setWeatherTag() {
    DateRecord recordData = createSampleRecord();

    recordData.setWeatherTag("맑음");

    assertThat(recordData.getWeatherTag()).isEqualTo("맑음");
  }

  @Test
  @DisplayName("기록 소유자 확인 - 본인")
  void isOwnedBy_true() {
    DateRecord recordData = createSampleRecord();

    assertThat(recordData.isOwnedBy(new UserId(1L))).isTrue();
  }

  @Test
  @DisplayName("기록 소유자 확인 - 타인")
  void isOwnedBy_false() {
    DateRecord recordData = createSampleRecord();

    assertThat(recordData.isOwnedBy(new UserId(999L))).isFalse();
  }

  @Test
  @DisplayName("커플 소유 기록 확인 - 본인 커플")
  void isOwnedByCouple_true() {
    DateRecord recordData = createSampleRecord();

    assertThat(recordData.isOwnedByCouple(new CoupleId(1L))).isTrue();
  }

  @Test
  @DisplayName("커플 소유 기록 확인 - 다른 커플")
  void isOwnedByCouple_false() {
    DateRecord recordData = createSampleRecord();

    assertThat(recordData.isOwnedByCouple(new CoupleId(999L))).isFalse();
  }
}