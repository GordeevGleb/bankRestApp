package org.example.bank_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bank_app.entity.enums.Roles;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Данные для обновления пользователя")
public class UpdateUserDto {

    @Schema(description = "Новый логин пользователя", example = "janedoe")
    private String login;

    @Schema(description = "Новое имя пользователя", example = "Jane Doe")
    private String name;

    @Schema(description = "Набор новых ролей", example = "[\"ADMIN\"]")
    private Set<Roles> roles;

    @Schema(description = "Флаг блокировки", example = "true")
    private Boolean isBlocked;
}

