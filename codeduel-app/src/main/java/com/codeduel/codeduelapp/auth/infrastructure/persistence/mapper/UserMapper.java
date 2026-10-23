package com.codeduel.codeduelapp.auth.infrastructure.persistence.mapper;

import com.codeduel.codeduelapp.auth.domain.model.User;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.ProfileJpaEntity;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toModel(UserJpaEntity e) {
        ProfileJpaEntity p = e.getProfile();
        return User.builder()
                .id(e.getId())
                .username(e.getUsername())
                .email(e.getEmail())
                .passwordHash(e.getPasswordHash())
                .role(e.getRole())
                .rating(p.getRating())
                .wins(p.getWins())
                .losses(p.getLosses())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public UserJpaEntity toNewEntity(User user) {
        UserJpaEntity u = new UserJpaEntity();
        u.setUsername(user.getUsername());
        u.setEmail(user.getEmail());
        u.setPasswordHash(user.getPasswordHash());
        u.setRole(user.getRole());

        ProfileJpaEntity p = new ProfileJpaEntity();
        p.setRating(user.getRating());
        p.setWins(user.getWins());
        p.setLosses(user.getLosses());
        u.attachProfile(p);
        return u;
    }

    public void updateEntity(UserJpaEntity u, User user) {
        u.setUsername(user.getUsername());
        u.setEmail(user.getEmail());
        u.setPasswordHash(user.getPasswordHash());
        u.setRole(user.getRole());
        u.getProfile().setRating(user.getRating());
        u.getProfile().setWins(user.getWins());
        u.getProfile().setLosses(user.getLosses());
    }
}
