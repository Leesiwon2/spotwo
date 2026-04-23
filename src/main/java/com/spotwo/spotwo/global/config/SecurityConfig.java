package com.spotwo.spotwo.global.config;

import com.spotwo.spotwo.domain.user.User;
import com.spotwo.spotwo.domain.user.UserRepository;
import com.spotwo.spotwo.global.exception.CustomException;
import com.spotwo.spotwo.global.exception.ErrorCode;
import com.spotwo.spotwo.global.jwt.JwtAuthenticationFilter;
import com.spotwo.spotwo.global.jwt.JwtToken;
import com.spotwo.spotwo.global.jwt.JwtTokenProvider;
import com.spotwo.spotwo.infrastructure.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomOAuth2UserService oAuth2UserService;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  @Value("${app.base-url}")  // ← 추가!
  private String baseUrl;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s ->
            s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers(
                "/api/auth/**",
                "/login/oauth2/**",
                "/oauth2/**",
                "/api/couples/invite/**",
                "/api/files/**"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth -> oauth
            .userInfoEndpoint(u -> u.userService(oAuth2UserService))
            .successHandler(oAuth2SuccessHandler())
        )
        .addFilterBefore(jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationSuccessHandler oAuth2SuccessHandler() {
    return (request, response, authentication) -> {
      DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
      String providerId = oAuth2User.getAttribute("id").toString();

      User user = userRepository.findByProviderId(providerId)
          .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

      JwtToken token = jwtTokenProvider.generateToken(user);
      response.sendRedirect(baseUrl + "/oauth2/redirect?token=" + token.getAccessToken());
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}