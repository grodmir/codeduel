package com.codeduel.codeduelapp.auth.infrastructure.security;

import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {
    private final UserJpaRepository repository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserJpaEntity u = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return User.withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .roles(u.getRole().name())
                .build();
    }
}
