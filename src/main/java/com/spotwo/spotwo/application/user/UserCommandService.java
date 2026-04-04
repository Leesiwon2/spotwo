package com.spotwo.spotwo.application.user;

import com.spotwo.spotwo.application.user.dto.LoginCommand;
import com.spotwo.spotwo.application.user.dto.SignupCommand;
import com.spotwo.spotwo.domain.user.Email;
import com.spotwo.spotwo.domain.user.User;
import com.spotwo.spotwo.domain.user.UserRepository;
import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;
import com.spotwo.spotwo.global.jwt.JwtToken;
import com.spotwo.spotwo.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public JwtToken signup(SignupCommand command) {
    Email email = new Email(command.email());

    if (userRepository.existsByEmail(email)) {
      throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    User user = User.ofLocal(
        command.email(),
        passwordEncoder.encode(command.password()),
        command.nickname()
    );

    User saved = userRepository.save(user);
    return jwtTokenProvider.generateToken(saved);
  }

  public JwtToken login(LoginCommand command) {
    Email email = new Email(command.email());

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    if (!passwordEncoder.matches(command.password(), user.getPassword())) {
      throw new CustomException(ErrorCode.INVALID_PASSWORD);
    }

    return jwtTokenProvider.generateToken(user);
  }
}