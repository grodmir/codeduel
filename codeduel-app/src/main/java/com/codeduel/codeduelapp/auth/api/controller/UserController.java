package com.codeduel.codeduelapp.auth.api.controller;

import com.codeduel.codeduelapp.auth.api.dto.UpdateUserRequest;
import com.codeduel.codeduelapp.auth.api.dto.UserResponse;
import com.codeduel.codeduelapp.auth.domain.model.User;
import com.codeduel.codeduelapp.auth.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public UserResponse getCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        User user = userService.getByUsername(jwt.getSubject());

        return UserResponse.from(user);
    }

    @PatchMapping
    public UserResponse updateCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        User user = userService.getByUsername(jwt.getSubject());

        User updatedUser = userService.update(
                user.getId(),
                request.email(),
                request.newPassword(),
                request.currentPassword()
        );
        return UserResponse.from(updatedUser);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        User user = userService.getByUsername(jwt.getSubject());

        userService.deleteById(user.getId());
    }
}
