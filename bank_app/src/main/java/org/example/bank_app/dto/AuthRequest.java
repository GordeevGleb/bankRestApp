package org.example.bank_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "AuthRequest", description = "Данные для аутентификации пользователя")
public class AuthRequest {

    @NotBlank(message = "Login required")
    @Schema(description = "Логин пользователя", example = "user123", required = true)
    private String login;

    @NotBlank(message = "Password required")
    @Schema(description = "Пароль пользователя", example = "password123", required = true)
    private String password;
}
