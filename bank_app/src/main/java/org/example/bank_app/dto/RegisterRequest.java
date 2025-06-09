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
@Schema(name = "RegisterRequest", description = "Данные для регистрации нового пользователя")
public class RegisterRequest {

    @NotBlank(message = "Login required")
    @Schema(description = "Логин нового пользователя", example = "newuser", required = true)
    private String login;

    @NotBlank(message = "Password required")
    @Schema(description = "Пароль нового пользователя", example = "newpassword", required = true)
    private String password;

    @Schema(description = "Имя пользователя", example = "Иван Иванов")
    private String name;
}
