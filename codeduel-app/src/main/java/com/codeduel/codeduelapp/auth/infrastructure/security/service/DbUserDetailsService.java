package com.codeduel.codeduelapp.auth.infrastructure.security.service;

import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.repository.UserJpaRepository;
import com.codeduel.codeduelapp.auth.infrastructure.security.model.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Загружает пользователя из БД для проверки пароля при логине.
 * В проверке входящих JWT не участвует.
 */
@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {
    private final UserJpaRepository repository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserJpaEntity u = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new AppUserDetails(u);
    }
}
