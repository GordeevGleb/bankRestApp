package org.example.bank_app.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import lombok.Data;

@Schema(description = "DTO для создания новой карты")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
 public class NewCardDto {
    @Schema(description = "Номер карты", example = "1234 5678 9012 3456")
    @NotBlank(message = "Card number required")
    @Pattern(regexp = "\\d{4} \\d{4} \\d{4} \\d{4}", message = "Card number must be in format '1234 5678 9012 3456'")
    private String cardNumber;

    @Schema(description = "Дата окончания срока действия карты", example = "2026-12-31")
    @Future(message = "Date must be in future")
    private LocalDate expiryDate;

    @Schema(description = "Начальный баланс карты", example = "1000.00")
    private Double balance;
}
