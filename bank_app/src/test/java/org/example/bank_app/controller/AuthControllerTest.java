package org.example.bank_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bank_app.dto.AuthRequest;
import org.example.bank_app.dto.AuthResponse;
import org.example.bank_app.dto.RegisterRequest;
import org.example.bank_app.exception.ConcurrentException;
import org.example.bank_app.security.JwtAuthFilter;
import org.example.bank_app.service.AuthService;
import org.example.bank_app.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;


    @MockBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void login_ReturnsAuthResponse() throws Exception {
        AuthRequest request = new AuthRequest("testuser", "password123");
        AuthResponse expectedResponse = new AuthResponse("jwt-token");

        Mockito.when(authService.login(any(AuthRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }


    @Test
    void register_ReturnsAuthResponse() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "password123", "test@example.com");
        AuthResponse expectedResponse = new AuthResponse("jwt-token");

        Mockito.when(authService.register(any(RegisterRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void register_ExistingUser_ReturnsConflict() throws Exception {
        RegisterRequest request = new RegisterRequest("existinguser", "password123", "existing@example.com");

        Mockito.when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new ConcurrentException("user already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("user already exists"));
    }
}




