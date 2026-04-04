package com.spotwo.spotwo.infrastructure.oauth;

import com.spotwo.spotwo.domain.user.User;
import com.spotwo.spotwo.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest request)
      throws OAuth2AuthenticationException {

    OAuth2User oAuth2User = super.loadUser(request);
    KakaoOAuth2UserInfo userInfo =
        new KakaoOAuth2UserInfo(oAuth2User.getAttributes());

    User user = userRepository.findByProviderId(userInfo.getProviderId())
        .orElseGet(() -> userRepository.save(
            User.ofKakao(
                userInfo.getEmail(),
                userInfo.getNickname(),
                userInfo.getProfileImageUrl(),
                userInfo.getProviderId()
            )
        ));

    log.info("카카오 로그인 성공: {}", user.getEmailValue());

    return new DefaultOAuth2User(
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
        oAuth2User.getAttributes(),
        "id"
    );
  }
}