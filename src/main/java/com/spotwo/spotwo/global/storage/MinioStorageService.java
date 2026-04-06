package com.spotwo.spotwo.global.storage;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class MinioStorageService implements StorageService {

  private final MinioClient minioClient;

  @Value("${minio.bucket}")
  private String bucket;

  @Override
  public String upload(MultipartFile file, String directory) {
    try {
      // 버킷 없으면 자동 생성
      boolean found = minioClient.bucketExists(
          BucketExistsArgs.builder().bucket(bucket).build());
      if (!found) {
        minioClient.makeBucket(
            MakeBucketArgs.builder().bucket(bucket).build());
        log.info("MinIO 버킷 생성: {}", bucket);
      }

      // 파일명 UUID로 생성 (중복 방지)
      String fileName = directory + "/" + UUID.randomUUID()
          + getExtension(file.getOriginalFilename());

      // 업로드
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucket)
              .object(fileName)
              .stream(file.getInputStream(), file.getSize(), -1)
              .contentType(file.getContentType())
              .build()
      );

      // URL 반환
      String url = minioClient.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .bucket(bucket)
              .object(fileName)
              .method(Method.GET)
              .expiry(7, TimeUnit.DAYS)
              .build()
      );

      log.info("MinIO 업로드 성공: {}", fileName);
      return url;

    } catch (Exception e) {
      log.error("MinIO 업로드 실패: {}", e.getMessage());
      throw new RuntimeException("파일 업로드에 실패했습니다.", e);
    }
  }

  @Override
  public void delete(String fileUrl) {
    try {
      // URL에서 파일명 추출
      String fileName = fileUrl.substring(fileUrl.indexOf(bucket)
          + bucket.length() + 1);

      if (fileName.contains("?")) {
        fileName = fileName.substring(0, fileName.indexOf("?"));
      }
      
      minioClient.removeObject(
          RemoveObjectArgs.builder()
              .bucket(bucket)
              .object(fileName)
              .build()
      );
      log.info("MinIO 삭제 성공: {}", fileName);
    } catch (Exception e) {
      log.error("MinIO 삭제 실패: {}", e.getMessage());
    }
  }

  private String getExtension(String fileName) {
    if (fileName == null || !fileName.contains(".")) return "";
    return fileName.substring(fileName.lastIndexOf("."));
  }
}