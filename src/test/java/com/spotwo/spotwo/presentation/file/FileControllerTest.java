package com.spotwo.spotwo.presentation.file;

import com.spotwo.spotwo.domain.user.UserRepository;
import com.spotwo.spotwo.global.jwt.JwtTokenProvider;
import com.spotwo.spotwo.global.storage.StorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private StorageService storageService;

  // JwtAuthenticationFilter가 필요로 하는 빈들 Mock으로 추가!
  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  private UserRepository userRepository;

  @Test
  @DisplayName("이미지 파일 업로드 성공")
  @WithMockUser
  void upload_success() throws Exception {
    // given
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "test-image.jpg",
        "image/jpeg",
        "fake-image-content".getBytes()
    );

    given(storageService.upload(any(), any()))
        .willReturn("http://localhost:9000/spotwo-photos/records/uuid.jpg");

    // when & then
    mockMvc.perform(multipart("/api/files/upload")
            .file(file)
            .param("directory", "records")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("업로드 성공!"))
        .andExpect(jsonPath("$.data").value(
            "http://localhost:9000/spotwo-photos/records/uuid.jpg"));
  }

  @Test
  @DisplayName("빈 파일 업로드 실패")
  @WithMockUser
  void upload_fail_emptyFile() throws Exception {
    // given
    MockMultipartFile emptyFile = new MockMultipartFile(
        "file",
        "empty.jpg",
        "image/jpeg",
        new byte[0]  // 빈 파일!
    );

    // when & then
    mockMvc.perform(multipart("/api/files/upload")
            .file(emptyFile)
            .with(csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("파일이 비어있습니다."));
  }

  @Test
  @DisplayName("이미지가 아닌 파일 업로드 실패")
  @WithMockUser
  void upload_fail_notImageFile() throws Exception {
    // given
    MockMultipartFile textFile = new MockMultipartFile(
        "file",
        "test.txt",
        "text/plain",  // 이미지 아님!
        "hello".getBytes()
    );

    // when & then
    mockMvc.perform(multipart("/api/files/upload")
            .file(textFile)
            .with(csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("이미지 파일만 업로드 가능합니다."));
  }

  @Test
  @DisplayName("10MB 초과 파일 업로드 실패")
  @WithMockUser
  void upload_fail_fileTooLarge() throws Exception {
    // given
    byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
    MockMultipartFile largeFile = new MockMultipartFile(
        "file",
        "large.jpg",
        "image/jpeg",
        largeContent
    );

    // when & then
    mockMvc.perform(multipart("/api/files/upload")
            .file(largeFile)
            .with(csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("파일 크기는 10MB 이하여야 합니다."));
  }
}