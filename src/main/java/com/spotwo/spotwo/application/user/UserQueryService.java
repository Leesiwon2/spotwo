package com.spotwo.spotwo.application.user;

import com.spotwo.spotwo.application.user.dto.UserResult;
import com.spotwo.spotwo.domain.user.User;
import com.spotwo.spotwo.domain.user.UserId;
import com.spotwo.spotwo.domain.user.UserRepository;
import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

  private final UserRepository userRepository;

  public UserResult getProfile(Long userId) {
    User user = userRepository.findById(new UserId(userId))
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    return UserResult.from(user);
  }
}