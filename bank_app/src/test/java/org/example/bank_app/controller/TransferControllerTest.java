package org.example.bank_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bank_app.dto.TransferDto;
import org.example.bank_app.security.JwtAuthFilter;
import org.example.bank_app.security.UserSecurity;
import org.example.bank_app.service.TransferService;
import org.example.bank_app.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@WebMvcTest(TransferController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserSecurity userSecurity;

    @Autowired
    private ObjectMapper objectMapper;

    private TransferDto requestDto;
    private TransferDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new TransferDto("1111-1111-1111-1111", "2222-2222-2222-2222", 100.0);
        responseDto = new TransferDto("1111-1111-1111-1111", "2222-2222-2222-2222", 100.0);
    }

    @Test
    @WithMockUser(username = "testuser")
    void transfer_ValidRequest_ReturnsTransferDto() throws Exception {
        Mockito.when(userSecurity.isBlocked("testuser")).thenReturn(true);
        Mockito.when(transferService.transfer(any(TransferDto.class), eq("testuser")))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromNumber").value("1111-1111-1111-1111"))
                .andExpect(jsonPath("$.toNumber").value("2222-2222-2222-2222"))
                .andExpect(jsonPath("$.difference").value(100.0));
    }
}




