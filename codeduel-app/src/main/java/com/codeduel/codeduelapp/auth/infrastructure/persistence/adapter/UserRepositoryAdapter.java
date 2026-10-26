package com.codeduel.codeduelapp.auth.infrastructure.persistence.adapter;

import com.codeduel.codeduelapp.auth.domain.exception.UserNotFoundById;
import com.codeduel.codeduelapp.auth.domain.model.User;
import com.codeduel.codeduelapp.auth.domain.repository.UserRepository;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.mapper.UserMapper;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * JPA-реализация {@link UserRepository}.
 *
 * <p>Чтения полагаются на {@code @EntityGraph} в {@link UserJpaRepository}:
 * профиль загружается eager'ом, иначе {@link UserMapper} упадёт на
 * {@code getProfile()} вне транзакции.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserMapper userMapper;
    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity entity;
        if (user.getId() == null) {
            entity = userMapper.toNewEntity(user);
            log.debug("Creating new user entity with id: {}", entity.getId());
        } else {
            entity = userJpaRepository.findById(user.getId()).orElseThrow(
                    () -> new UserNotFoundById(user.getId())
            );
            userMapper.updateEntity(entity, user);
            log.debug("Updating user entity with id: {}", entity.getId());
        }

        return userMapper.toModel(userJpaRepository.save(entity));
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userJpaRepository.findById(id).map(userMapper::toModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userMapper::toModel);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(userMapper::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        UserJpaEntity entity = userJpaRepository.findById(id).orElseThrow(
                () -> new UserNotFoundById(id)
        );
        entity.setDeletedAt(OffsetDateTime.now());
        log.debug("User soft-deleted, userId ={}", entity.getId());
    }
}
