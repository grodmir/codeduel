package com.codeduel.codeduelapp.auth.infrastructure.security;

import com.codeduel.codeduelapp.auth.domain.model.Role;
import com.codeduel.codeduelapp.auth.infrastructure.persistence.entity.UserJpaEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AppUserDetails implements UserDetails {

    private final Integer id;
    private final String username;
    private final String passwordHash;
    private final Role role;

    public AppUserDetails(UserJpaEntity u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.passwordHash = u.getPasswordHash();
        this.role = u.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }
}