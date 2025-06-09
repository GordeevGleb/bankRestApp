package org.example.bank_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bank_app.entity.User;
import org.example.bank_app.entity.enums.CardStatus;

import java.time.LocalDate;

@Schema(description = "DTO для карты пользователя")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCardDto {
    @Schema(description = "ID карты", example = "1")
    private Long id;

    @Schema(description = "Номер карты", example = "1234 5678 9012 3456")
    private String number;

    @Schema(description = "Пользователь-владелец карты")
    private User user;

    @Schema(description = "Дата окончания срока действия карты", example = "2026-12-31")
    private LocalDate expiryDate;

    @Schema(description = "Статус карты")
    private CardStatus status;

    @Schema(description = "Баланс карты", example = "1500.00")
    private Double balance;
}
