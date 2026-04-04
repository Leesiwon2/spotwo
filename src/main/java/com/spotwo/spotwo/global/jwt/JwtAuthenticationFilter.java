package com.spotwo.spotwo.global.jwt;

import com.spotwo.spotwo.domain.user.User;
import com.spotwo.spotwo.domain.user.UserId;
import com.spotwo.spotwo.domain.user.UserRepository;
import com.spotwo.spotwo.global.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {
    try {
      String token = resolveToken(request);
      if (token != null && jwtTokenProvider.validateToken(token)) {
        Long userId = jwtTokenProvider.getUserId(token);
        User user = userRepository.findById(new UserId(userId))
            .orElseThrow();

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                user, null,
                List.of(new SimpleGrantedAuthority(
                    "ROLE_" + user.getRole().name()))
            );
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    } catch (CustomException e) {
      log.warn("JWT 인증 실패: {}", e.getMessage());
    }
    chain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }
    return null;
  }
}