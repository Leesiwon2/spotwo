package com.spotwo.spotwo.infrastructure.persistence.record;

import com.spotwo.spotwo.domain.record.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "date_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class DateRecordJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long coupleId;

  @Column(nullable = false)
  private Long writerId;

  private String regionCode;
  private String regionName;
  private Double latitude;
  private Double longitude;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  private String thumbnailUrl;
  private LocalDate visitDate;

  @Enumerated(EnumType.STRING)
  private Season season;

  private String weatherTag;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RecordType recordType;

  @ElementCollection
  @CollectionTable(name = "record_photos",
      joinColumns = @JoinColumn(name = "record_id"))
  @Column(name = "photo_url")
  private List<String> photoUrls = new ArrayList<>();

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  public static DateRecordJpaEntity fromDomain(DateRecord recordData) {
    DateRecordJpaEntity entity = new DateRecordJpaEntity();
    if (recordData.getId() != null) entity.id = recordData.getId().value();
    entity.coupleId = recordData.getCoupleId().value();
    entity.writerId = recordData.getWriterId().value();
    entity.regionCode = recordData.getRegionCode();
    entity.regionName = recordData.getRegionName();
    entity.latitude = recordData.getLatitude();
    entity.longitude = recordData.getLongitude();
    entity.title = recordData.getTitle();
    entity.content = recordData.getContent();
    entity.thumbnailUrl = recordData.getThumbnailUrl();
    entity.visitDate = recordData.getVisitDate();
    entity.season = recordData.getSeason();
    entity.weatherTag = recordData.getWeatherTag();
    entity.recordType = recordData.getRecordType();
    entity.photoUrls = new ArrayList<>(recordData.getPhotoUrls());
    return entity;
  }

  public DateRecord toDomain() {
    return DateRecord.reconstruct(
        id, coupleId, writerId,
        regionCode, regionName,
        latitude, longitude,
        title, content, thumbnailUrl,
        visitDate, season, weatherTag,
        recordType, photoUrls,
        createdAt, updatedAt
    );
  }
}