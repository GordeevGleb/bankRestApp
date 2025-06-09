package org.example.bank_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bank_app.dto.CardDto;
import org.example.bank_app.dto.NewCardDto;
import org.example.bank_app.dto.UserCardDto;
import org.example.bank_app.entity.enums.CardStatus;
import org.example.bank_app.security.JwtAuthFilter;
import org.example.bank_app.service.CardService;
import org.example.bank_app.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private Authentication authentication;

    @Autowired
    private ObjectMapper objectMapper;

    private NewCardDto newCardDto;
    private CardDto cardDto;

    @BeforeEach
    void setUp() {
        newCardDto = NewCardDto.builder()
                .cardNumber("1111 1111 1111 1111")
                .expiryDate(LocalDate.now().plusDays(30))
                .balance(100.0)
                .build();
        cardDto = CardDto.builder()
                .id(1L)
                .number("1111 1111 1111 1111")
                .expiryDate(LocalDate.now().plusDays(30))
                .balance(100.0)
                .status(CardStatus.ACTIVE)
                .build();
    }


    @Test
    void updateStatus_ValidStatus_ReturnsUpdatedCard() throws Exception {
        CardStatus newStatus = CardStatus.ACTIVE;
        CardDto updatedCardDto = CardDto.builder()
                .id(1L)
                .number("1111 1111 1111 1111")
                .expiryDate(LocalDate.now().plusDays(30))
                .balance(100.0)
                .status(CardStatus.BLOCKED)
                .build();

        Mockito.when(cardService.updateStatus(eq(1L), eq(newStatus))).thenReturn(updatedCardDto);

        mockMvc.perform(patch("/api/cards/1/status")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    void updateStatus_InvalidCardId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/api/cards/-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_MissingStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/api/cards/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_ValidCardId_ReturnsNoContent() throws Exception {
        Mockito.doNothing().when(cardService).delete(1L);

        mockMvc.perform(delete("/api/cards/1"))
                .andExpect(status().isOk());
    }
}

