package org.example.bank_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank_app.dto.AuthRequest;
import org.example.bank_app.dto.AuthResponse;
import org.example.bank_app.dto.RegisterRequest;
import org.example.bank_app.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Аутентификация", description = "Эндпоинты регистрации и логина")
public class AuthController {

    private final AuthService authService;


    @Operation(
            summary = "Вход пользователя",
            description = "Аутентификация пользователя с помощью логина и пароля",
            requestBody = @RequestBody(
                    description = "Данные для входа пользователя",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный вход",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
            }
    )
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        log.info("/auth/login; {}", request);
        return authService.login(request);
    }

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Регистрация пользователя с необходимыми данными",
            requestBody = @RequestBody(
                    description = "Данные для регистрации пользователя",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешная регистрация",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные регистрации")
            }
    )
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public AuthResponse register(@RequestBody RegisterRequest request) {
        log.info("/auth/register; {}", request);
       return authService.register(request);
    }
}

