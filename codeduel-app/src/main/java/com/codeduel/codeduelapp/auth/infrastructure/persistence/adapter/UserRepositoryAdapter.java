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
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserMapper userMapper;
    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public User create(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("New user must not have id");
        }
        UserJpaEntity userJpaEntity = userMapper.toNewEntity(user);
        return userMapper.toModel(userJpaRepository.save(userJpaEntity));
    }

    @Override
    @Transactional
    public User update(User user) {
        UserJpaEntity userEntity = userJpaRepository.findById(user.getId()).orElseThrow(
                () -> new UserNotFoundById(user.getId())
        );
        userMapper.updateEntity(userEntity, user);
        return userMapper.toModel(userEntity);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userJpaRepository.findById(id).map(userMapper::toModel);
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
