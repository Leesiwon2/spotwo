package com.spotwo.spotwo.infrastructure.oauth;

import java.util.Map;

public class KakaoOAuth2UserInfo {

  private final Map<String, Object> attributes;
  private final Map<String, Object> kakaoAccount;
  private final Map<String, Object> kakaoProfile;

  @SuppressWarnings("unchecked")
  public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
    this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    this.kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
  }

  public String getProviderId() { return attributes.get("id").toString(); }
  public String getEmail() { return (String) kakaoAccount.get("email"); }
  public String getNickname() { return (String) kakaoProfile.get("nickname"); }
  public String getProfileImageUrl() {
    return (String) kakaoProfile.get("profile_image_url");
  }
}