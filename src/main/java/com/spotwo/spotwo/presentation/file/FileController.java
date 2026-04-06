package com.spotwo.spotwo.presentation.file;

import com.spotwo.spotwo.global.response.ApiResponse;
import com.spotwo.spotwo.global.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

  private final StorageService storageService;

  @PostMapping("/upload")
  public ResponseEntity<ApiResponse<String>> upload(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "directory", defaultValue = "records") String directory) {

    // 파일 유효성 검사
    if (file.isEmpty()) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.fail("파일이 비어있습니다."));
    }

    // 이미지 파일만 허용
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.fail("이미지 파일만 업로드 가능합니다."));
    }

    // 파일 크기 제한 (10MB)
    if (file.getSize() > 10 * 1024 * 1024) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.fail("파일 크기는 10MB 이하여야 합니다."));
    }

    String url = storageService.upload(file, directory);
    return ResponseEntity.ok(ApiResponse.ok("업로드 성공!", url));
  }
}