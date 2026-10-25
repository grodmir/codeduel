package com.codeduel.codeduelapp.auth.domain.service;

import com.codeduel.codeduelapp.auth.domain.exception.InvalidCurrentPasswordException;
import com.codeduel.codeduelapp.auth.domain.exception.UserArleadyExistsException;
import com.codeduel.codeduelapp.auth.domain.exception.UserNotFoundById;
import com.codeduel.codeduelapp.auth.domain.exception.UserNotFoundByUsername;
import com.codeduel.codeduelapp.auth.domain.model.Role;
import com.codeduel.codeduelapp.auth.domain.model.User;
import com.codeduel.codeduelapp.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRevoker tokenRevoker;

    @Value("${elo.initial}")
    private int initialRating;

    @Transactional
    public void register(String username, String email, String password) {
        String normUsername = username.trim();
        String normEmail = email.trim().toLowerCase();

        if (userRepository.existsByUsername(normUsername)) throw new UserArleadyExistsException("USERNAME");
        if (userRepository.existsByEmail(normEmail)) throw new UserArleadyExistsException("EMAIL");

        User user = User.builder()
                .username(normUsername)
                .email(normEmail)
                .passwordHash(passwordEncoder.encode(password))
                .role(Role.USER)
                .rating(initialRating)
                .wins(0)
                .losses(0)
                .build();
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getById(Integer id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundById(id)
        );
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundByUsername(username)
        );
    }

    @Transactional
    public User update(Integer id, String email, String newPassword, String currentPassword) {
        User user = getById(id);
        var builder = user.toBuilder();

        if (email != null) {
            String normEmail = email.trim().toLowerCase();
            if (!normEmail.equals(user.getEmail())) {
                if (userRepository.existsByEmail(normEmail)) throw new UserArleadyExistsException("EMAIL");
                builder.email(normEmail);
            }
        }

        if (newPassword != null) {
            if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                throw new InvalidCurrentPasswordException();
            }
            builder.passwordHash(passwordEncoder.encode(newPassword));
            tokenRevoker.revokeAllForUser(user.getId());
        }

        return userRepository.save(builder.build());
    }

    @Transactional
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
        tokenRevoker.revokeAllForUser(id);
    }
}
