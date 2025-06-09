package org.example.bank_app.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank_app.dto.UpdateUserDto;
import org.example.bank_app.dto.UserDto;
import org.example.bank_app.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Обновить данные пользователя",
            description = "Обновление данных пользователя по ID. Только для администраторов.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "userId", description = "ID пользователя", required = true)
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "Обновленные данные пользователя",
                    content = @Content(schema = @Schema(implementation = UpdateUserDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён",
                            content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
            }
    )
    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto update(@PathVariable @Positive Long userId,
                          @RequestBody UpdateUserDto updateUserDto) {
        log.info("/users/{} new params {}", userId, updateUserDto);
        return userService.update(userId, updateUserDto);
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаление пользователя по ID. Только для администраторов.",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "userId", description = "ID пользователя", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно удалён"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
            }
    )
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable @Positive Long userId) {
        log.info("/users/{} delete", userId);
        userService.delete(userId);
    }
}

