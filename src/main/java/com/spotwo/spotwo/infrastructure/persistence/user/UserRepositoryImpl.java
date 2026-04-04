package com.spotwo.spotwo.infrastructure.persistence.user;

import com.spotwo.spotwo.domain.user.Email;
import com.spotwo.spotwo.domain.user.User;
import com.spotwo.spotwo.domain.user.UserId;
import com.spotwo.spotwo.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository userJpaRepository;

  @Override
  public User save(User user) {
    UserJpaEntity entity = UserJpaEntity.fromDomain(user);
    return userJpaRepository.save(entity).toDomain();
  }

  @Override
  public Optional<User> findById(UserId id) {
    return userJpaRepository.findById(id.value())
        .map(UserJpaEntity::toDomain);
  }

  @Override
  public Optional<User> findByEmail(Email email) {
    return userJpaRepository.findByEmail(email.value())
        .map(UserJpaEntity::toDomain);
  }

  @Override
  public Optional<User> findByProviderId(String providerId) {
    return userJpaRepository.findByProviderId(providerId)
        .map(UserJpaEntity::toDomain);
  }

  @Override
  public boolean existsByEmail(Email email) {
    return userJpaRepository.existsByEmail(email.value());
  }
}