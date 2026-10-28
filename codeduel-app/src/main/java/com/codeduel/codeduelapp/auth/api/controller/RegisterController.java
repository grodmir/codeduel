package com.codeduel.codeduelapp.auth.api.controller;

import com.codeduel.codeduelapp.auth.api.dto.ApiError;
import com.codeduel.codeduelapp.auth.api.dto.RegisterRequest;
import com.codeduel.codeduelapp.auth.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class RegisterController {
    private final UserService userService;

    @Operation(summary = "Регистрация нового пользователя и связанного с ним профиля",
            description = "Создаёт пользователя с ролью USER и начальным рейтингом. Логин после регистрации отдельный — через /auth/login.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Невалидное тело запроса",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Username или email уже заняты",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest req) {
        userService.create(
                req.username(),
                req.email(),
                req.password()
        );
    }
}
