package com.codeduel.codeduelapp.auth.infrastructure.persistence.adapter;

import com.codeduel.codeduelapp.auth.domain.UserRepository;
import com.codeduel.codeduelapp.auth.domain.model.User;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.exception.UserNotFoundException;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.mapper.UserMapper;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        } else {
            entity = userJpaRepository.findById(user.getId()).orElseThrow(
                    () -> new UserNotFoundException(user.getId())
            );
            userMapper.updateEntity(entity, user);
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
}
