package org.example.bank_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bank_app.entity.Card;
import org.example.bank_app.entity.enums.Roles;

import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Информация о пользователе")
public class UserDto {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Логин пользователя", example = "johndoe")
    private String login;

    @Schema(description = "Имя пользователя", example = "John Doe")
    private String name;

    @Schema(description = "Список карт пользователя")
    private List<Card> cards;

    @Schema(description = "Роли пользователя", example = "[\"ADMIN\", \"USER\"]")
    private Set<Roles> roles;

    @Schema(description = "Заблокирован ли пользователь", example = "false")
    private Boolean isBlocked;
}

