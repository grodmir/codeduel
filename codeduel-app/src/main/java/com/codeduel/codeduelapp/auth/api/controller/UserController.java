package com.codeduel.codeduelapp.auth.api.controller;

import com.codeduel.codeduelapp.auth.api.dto.ApiError;
import com.codeduel.codeduelapp.auth.api.dto.UpdateUserRequest;
import com.codeduel.codeduelapp.auth.api.dto.UserResponse;
import com.codeduel.codeduelapp.auth.domain.model.Profile;
import com.codeduel.codeduelapp.auth.domain.model.User;
import com.codeduel.codeduelapp.auth.domain.service.ProfileService;
import com.codeduel.codeduelapp.auth.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Профиль текущего аутентифицированного пользователя")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;

    @Operation(summary = "Получить текущего пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или невалиден",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public UserResponse getCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        User user = userService.getById(Integer.valueOf(jwt.getSubject()));
        Profile profile = profileService.getByUserId(user.getId());

        return UserResponse.from(user, profile);
    }

    @Operation(summary = "Обновить профиль",
            description = "Частичное обновление: null-поля не меняются. Смена пароля требует currentPassword и отзывает все активные токены пользователя.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обновлённый профиль"),
            @ApiResponse(responseCode = "400", description = "Невалидное тело запроса или неверный currentPassword",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или невалиден",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Новый email уже занят",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PatchMapping
    public UserResponse updateCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        User updatedUser = userService.update(
                Integer.valueOf(jwt.getSubject()),
                request.email(),
                request.newPassword(),
                request.currentPassword()
        );
        Profile profile = profileService.getByUserId(updatedUser.getId());
        return UserResponse.from(updatedUser,  profile);
    }

    @Operation(summary = "Удалить аккаунт",
            description = "Soft delete: помечает пользователя удалённым и отзывает все активные токены.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Аккаунт помечен удалённым"),
            @ApiResponse(responseCode = "401", description = "Токен отсутствует или невалиден",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        userService.deleteById(Integer.valueOf(jwt.getSubject()));
    }
}
