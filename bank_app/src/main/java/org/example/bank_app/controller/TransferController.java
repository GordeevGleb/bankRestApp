package org.example.bank_app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank_app.dto.TransferDto;
import org.example.bank_app.service.TransferService;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
@Slf4j
public class TransferController {

    private final TransferService transferService;

    @Operation(
            summary = "Перевод между картами",
            description = "Выполнение перевода средств с одной карты на другую. Доступ разрешён только не заблокированным пользователям.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Данные для перевода",
                    content = @Content(schema = @Schema(implementation = TransferDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен",
                            content = @Content(schema = @Schema(implementation = TransferDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные для перевода"),
                    @ApiResponse(responseCode = "403", description = "Пользователь заблокирован")
            }
    )
    @PatchMapping
    @PreAuthorize("@userSecurity.isBlocked(authentication.name)")
    public TransferDto transfer(@RequestBody TransferDto transferDto) {
        log.info("/transfer/ {}", transferDto);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return transferService.transfer(transferDto, username);
    }
}

