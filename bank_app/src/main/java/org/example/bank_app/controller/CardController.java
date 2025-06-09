package org.example.bank_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank_app.dto.CardDto;
import org.example.bank_app.dto.NewCardDto;
import org.example.bank_app.dto.UserCardDto;
import org.example.bank_app.entity.enums.CardStatus;
import org.example.bank_app.service.CardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
@Slf4j
@Validated
public class CardController {

    private final CardService cardService;

    @Operation(
            summary = "Создание карты",
            description = "Создание новой карты для пользователя с указанным ID",
            requestBody = @RequestBody(
                    description = "Данные для создания карты",
                    required = true,
                    content = @Content(schema = @Schema(implementation = NewCardDto.class))
            ),
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "holderId", description = "ID владельца карты", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Карта успешно создана",
                            content = @Content(schema = @Schema(implementation = CardDto.class)))
            }
    )
    @PostMapping("/users/{holderId}/cards")
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto create(@Valid @RequestBody NewCardDto newCardDto,
                          @PathVariable @Positive Long holderId) {
        log.info("/cards/create;  {}, creator {}", newCardDto, holderId);
        return cardService.create(newCardDto, holderId);
    }

    @Operation(
            summary = "Обновление статуса карты",
            description = "Обновление статуса карты по ID карты",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "cardId", description = "ID карты", required = true),
                    @Parameter(in = ParameterIn.QUERY, name = "status", description = "Новый статус карты", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Статус успешно обновлен",
                            content = @Content(schema = @Schema(implementation = CardDto.class)))
            }
    )
    @PatchMapping("/{cardId}/status")
    @PreAuthorize("hasRole('ADMIN') or @cardSecurity.isCardOwnerIsNotBlocked(cardId, authentication.name)")
    public CardDto updateStatus(@PathVariable @Positive Long cardId,
                                @RequestParam @NotNull CardStatus status) {
        log.info("/admin/cards/updateStatus; {}, cardId {}", status, cardId);
        return cardService.updateStatus(cardId, status);
    }

    @Operation(
            summary = "Удаление карты",
            description = "Удаление карты по ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "cardId", description = "ID карты", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта успешно удалена")
            }
    )
    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable @Positive Long cardId) {
        log.info("/cards/delete {}", cardId);
        cardService.delete(cardId);
    }

    @Operation(
            summary = "Получить все карты",
            description = "Получение списка всех карт с возможностью фильтрации по cardId и постраничной навигацией",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "cardId", description = "Фильтрация по ID карты"),
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Номер страницы"),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Размер страницы")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список карт",
                            content = @Content(schema = @Schema(implementation = CardDto.class)))
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CardDto> getAll(@RequestParam(required = false) @Positive Long cardId,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        log.info("/cards/ getAll");
        return cardService.getAll(cardId, page, size);
    }

    @Operation(
            summary = "Получить карты текущего пользователя",
            description = "Получение списка карт, принадлежащих текущему пользователю (или админом)",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Номер страницы"),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Размер страницы")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список карт пользователя",
                            content = @Content(schema = @Schema(implementation = UserCardDto.class)))
            }
    )
    @GetMapping("/my")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public List<UserCardDto> getByHolder(Authentication authentication,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("/cards/getByHolder {}", authentication.getName());
        String login = authentication.getName();
        return cardService.getByHolder(login, page, size);
    }
}
