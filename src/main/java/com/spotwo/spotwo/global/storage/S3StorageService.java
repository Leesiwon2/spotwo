package com.spotwo.spotwo.global.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@Profile("prod")
@RequiredArgsConstructor
@Slf4j
public class S3StorageService implements StorageService {

  private final S3Client s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Value("${cloud.aws.region.static}")
  private String region;

  @Override
  public String upload(MultipartFile file, String directory) {
    try {
      String fileName = directory + "/" + UUID.randomUUID()
          + getExtension(file.getOriginalFilename());

      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(bucket)
              .key(fileName)
              .contentType(file.getContentType())
              .build(),
          RequestBody.fromInputStream(
              file.getInputStream(), file.getSize())
      );

      String url = String.format(
          "https://%s.s3.%s.amazonaws.com/%s",
          bucket, region, fileName);

      log.info("S3 업로드 성공: {}", fileName);
      return url;

    } catch (Exception e) {
      log.error("S3 업로드 실패: {}", e.getMessage());
      throw new RuntimeException("파일 업로드에 실패했습니다.", e);
    }
  }

  @Override
  public void delete(String fileUrl) {
    try {
      String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
      s3Client.deleteObject(
          DeleteObjectRequest.builder()
              .bucket(bucket)
              .key(fileName)
              .build()
      );
      log.info("S3 삭제 성공: {}", fileName);
    } catch (Exception e) {
      log.error("S3 삭제 실패: {}", e.getMessage());
    }
  }

  private String getExtension(String fileName) {
    if (fileName == null || !fileName.contains(".")) return "";
    return fileName.substring(fileName.lastIndexOf("."));
  }
}