package com.spotwo.spotwo.domain.user;

import java.util.Optional;

public interface UserRepository {
  User save(User user);
  Optional<User> findById(UserId id);
  Optional<User> findByEmail(Email email);
  Optional<User> findByProviderId(String providerId);
  boolean existsByEmail(Email email);
}