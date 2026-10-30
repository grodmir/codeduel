package com.codeduel.codeduelapp.auth.infrastructure.persistence.entity;

import com.codeduel.codeduelapp.auth.domain.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;


/**
 * Класс сущность, соответствующая таблице {@code users} в базе данных.
 * */
@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username",  nullable = false)
    private String username;

    @Column(name = "email",   nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
