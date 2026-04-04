package com.spotwo.spotwo.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
  Optional<UserJpaEntity> findByEmail(String email);
  Optional<UserJpaEntity> findByProviderId(String providerId);
  boolean existsByEmail(String email);
}