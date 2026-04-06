package com.spotwo.spotwo.global.storage;

import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MinioStorageServiceTest {

  @InjectMocks
  private MinioStorageService minioStorageService;

  @Mock
  private MinioClient minioClient;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(minioStorageService, "bucket", "spotwo-photos");
  }

  @Test
  @DisplayName("이미지 업로드 성공")
  void upload_success() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test-image.jpg", "image/jpeg",
        "fake-image-content".getBytes()
    );

    given(minioClient.bucketExists(any(BucketExistsArgs.class))).willReturn(true);
    given(minioClient.putObject(any(PutObjectArgs.class)))  // ← 수정!
        .willReturn(mock(ObjectWriteResponse.class));
    given(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
        .willReturn("http://localhost:9000/spotwo-photos/records/uuid.jpg");

    String url = minioStorageService.upload(file, "records");

    assertThat(url).isNotNull();
    assertThat(url).contains("spotwo-photos");
    assertThat(url).contains("records");
  }

  @Test
  @DisplayName("버킷 없으면 자동 생성 후 업로드")
  void upload_createBucketIfNotExists() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test-image.jpg", "image/jpeg",
        "fake-image-content".getBytes()
    );

    given(minioClient.bucketExists(any(BucketExistsArgs.class))).willReturn(false);
    willDoNothing().given(minioClient).makeBucket(any(MakeBucketArgs.class));
    given(minioClient.putObject(any(PutObjectArgs.class)))  // ← 수정!
        .willReturn(mock(ObjectWriteResponse.class));
    given(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
        .willReturn("http://localhost:9000/spotwo-photos/records/uuid.jpg");

    String url = minioStorageService.upload(file, "records");

    assertThat(url).isNotNull();
    then(minioClient).should().makeBucket(any(MakeBucketArgs.class));
  }

  @Test
  @DisplayName("업로드 실패 시 RuntimeException 발생")
  void upload_fail_throwsException() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test-image.jpg", "image/jpeg",
        "fake-image-content".getBytes()
    );

    given(minioClient.bucketExists(any(BucketExistsArgs.class)))
        .willThrow(new RuntimeException("MinIO 연결 실패"));

    assertThatThrownBy(() -> minioStorageService.upload(file, "records"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("파일 업로드에 실패했습니다.");
  }

  @Test
  @DisplayName("파일 확장자 올바르게 추출")
  void upload_correctExtension() throws Exception {
    MockMultipartFile pngFile = new MockMultipartFile(
        "file", "test-image.png", "image/png",
        "fake-image-content".getBytes()
    );

    given(minioClient.bucketExists(any(BucketExistsArgs.class))).willReturn(true);
    given(minioClient.putObject(any(PutObjectArgs.class)))  // ← 수정!
        .willReturn(mock(ObjectWriteResponse.class));
    given(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
        .willReturn("http://localhost:9000/spotwo-photos/records/uuid.png");

    minioStorageService.upload(pngFile, "records");

    then(minioClient).should().putObject(argThat(args ->
        args.object().endsWith(".png")
    ));
  }

  @Test
  @DisplayName("파일 삭제 성공")
  void delete_success() throws Exception {
    String fileUrl = "http://localhost:9000/spotwo-photos/records/uuid.jpg?token=xxx";

    // void 메서드는 willDoNothing() 사용!
    willDoNothing().given(minioClient)
        .removeObject(any(RemoveObjectArgs.class));

    assertThatNoException()
        .isThrownBy(() -> minioStorageService.delete(fileUrl));

    then(minioClient).should().removeObject(any(RemoveObjectArgs.class));
  }

  @Test
  @DisplayName("파일 삭제 실패해도 예외 안 터짐 (로그만)")
  void delete_fail_noException() throws Exception {
    String fileUrl = "http://localhost:9000/spotwo-photos/records/uuid.jpg?token=xxx";

    // void 메서드 예외는 willThrow() 사용!
    willThrow(new RuntimeException("삭제 실패"))
        .given(minioClient).removeObject(any(RemoveObjectArgs.class));

    assertThatNoException()
        .isThrownBy(() -> minioStorageService.delete(fileUrl));
  }
}