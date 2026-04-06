package com.spotwo.spotwo.domain.record;

import com.spotwo.spotwo.domain.couple.CoupleId;
import com.spotwo.spotwo.domain.user.UserId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DateRecord {

  private final RecordId id;
  private final CoupleId coupleId;
  private final UserId writerId;
  private String regionCode;
  private String regionName;
  private Double latitude;
  private Double longitude;
  private String title;
  private String content;
  private String thumbnailUrl;
  private LocalDate visitDate;
  private Season season;
  private String weatherTag;
  private RecordType recordType;
  private final List<String> photoUrls;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // 새 기록 생성
  public static DateRecord create(CoupleId coupleId, UserId writerId,
      String regionCode, String regionName,
      Double latitude, Double longitude,
      String title, String content,
      String thumbnailUrl, LocalDate visitDate,
      RecordType recordType) {
    return new DateRecord(
        null, coupleId, writerId,
        regionCode, regionName,
        latitude, longitude,
        title, content, thumbnailUrl,
        visitDate,
        Season.from(visitDate),  // 날짜로 계절 자동태깅
        null,                    // 날씨는 API 호출 후 설정
        recordType,
        new ArrayList<>(),
        LocalDateTime.now(), LocalDateTime.now()
    );
  }

  // DB 조회 후 재구성
  public static DateRecord reconstruct(Long id, Long coupleId, Long writerId,
      String regionCode, String regionName,
      Double latitude, Double longitude,
      String title, String content,
      String thumbnailUrl, LocalDate visitDate,
      Season season, String weatherTag,
      RecordType recordType, List<String> photoUrls,
      LocalDateTime createdAt, LocalDateTime updatedAt) {
    return new DateRecord(
        new RecordId(id), new CoupleId(coupleId), new UserId(writerId),
        regionCode, regionName, latitude, longitude,
        title, content, thumbnailUrl, visitDate,
        season, weatherTag, recordType,
        new ArrayList<>(photoUrls), createdAt, updatedAt
    );
  }

  private DateRecord(RecordId id, CoupleId coupleId, UserId writerId,
      String regionCode, String regionName,
      Double latitude, Double longitude,
      String title, String content,
      String thumbnailUrl, LocalDate visitDate,
      Season season, String weatherTag,
      RecordType recordType, List<String> photoUrls,
      LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.coupleId = coupleId;
    this.writerId = writerId;
    this.regionCode = regionCode;
    this.regionName = regionName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.title = title;
    this.content = content;
    this.thumbnailUrl = thumbnailUrl;
    this.visitDate = visitDate;
    this.season = season;
    this.weatherTag = weatherTag;
    this.recordType = recordType;
    this.photoUrls = photoUrls;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // 비즈니스 메서드
  public void update(String title, String content, String thumbnailUrl,
      String regionCode, String regionName,
      Double latitude, Double longitude, LocalDate visitDate) {
    this.title = title;
    this.content = content;
    this.thumbnailUrl = thumbnailUrl;
    this.regionCode = regionCode;
    this.regionName = regionName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.visitDate = visitDate;
    this.season = Season.from(visitDate); // 날짜 변경 시 계절 재계산
    this.updatedAt = LocalDateTime.now();
  }

  public void setWeatherTag(String weatherTag) {
    this.weatherTag = weatherTag;
  }

  public void toggleToWishlist() {
    this.recordType = RecordType.WISHLIST;
    this.updatedAt = LocalDateTime.now();
  }

  public void toggleToVisit() {
    this.recordType = RecordType.VISIT;
    this.updatedAt = LocalDateTime.now();
  }

  public boolean isOwnedBy(UserId userId) {
    return this.writerId.equals(userId);
  }

  public boolean isOwnedByCouple(CoupleId coupleId) {
    return this.coupleId.equals(coupleId);
  }

  // Getters
  public RecordId getId() { return id; }
  public CoupleId getCoupleId() { return coupleId; }
  public UserId getWriterId() { return writerId; }
  public String getRegionCode() { return regionCode; }
  public String getRegionName() { return regionName; }
  public Double getLatitude() { return latitude; }
  public Double getLongitude() { return longitude; }
  public String getTitle() { return title; }
  public String getContent() { return content; }
  public String getThumbnailUrl() { return thumbnailUrl; }
  public LocalDate getVisitDate() { return visitDate; }
  public Season getSeason() { return season; }
  public String getWeatherTag() { return weatherTag; }
  public RecordType getRecordType() { return recordType; }
  public List<String> getPhotoUrls() { return Collections.unmodifiableList(photoUrls); }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
}