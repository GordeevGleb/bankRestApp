package org.example.bank_app.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Данные для перевода между картами")
public class TransferDto {

    @Schema(description = "Номер карты отправителя", example = "1234 5678 9012 3456", minLength = 19, maxLength = 19)
    @NotBlank(message = "Card number required")
    @Pattern(regexp = "\\d{4} \\d{4} \\d{4} \\d{4}", message = "Card number must be in format '1234 5678 9012 3456'")
    private String fromNumber;

    @Schema(description = "Номер карты получателя", example = "6543 2109 8765 4321", minLength = 19, maxLength = 19)
    @NotBlank(message = "Card number required")
    @Pattern(regexp = "\\d{4} \\d{4} \\d{4} \\d{4}", message = "Card number must be in format '1234 5678 9012 3456'")
    private String toNumber;

    @Schema(description = "Сумма перевода", example = "100.00", minimum = "0")
    @NotNull(message = "Field can't be null")
    @Positive(message = "Value must be positive")
    private Double difference;
}

