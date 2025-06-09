package org.example.bank_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bank_app.dto.UpdateUserDto;
import org.example.bank_app.dto.UserDto;
import org.example.bank_app.security.JwtAuthFilter;
import org.example.bank_app.service.AuthService;
import org.example.bank_app.service.UserService;
import org.example.bank_app.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;


    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updateUser_WithValidData_ReturnsUpdatedUser() throws Exception {
        Long userId = 1L;
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("New Name");

        UserDto returnedUser = UserDto.builder()
                .id(userId)
                .name("New Name")
                .build();

        Mockito.when(userService.update(eq(userId), any(UpdateUserDto.class))).thenReturn(returnedUser);

        mockMvc.perform(patch("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("New Name"));

        Mockito.verify(userService).update(eq(userId), any(UpdateUserDto.class));
    }

    @Test
    void deleteUser_WithValidId_ReturnsOk() throws Exception {
        Long userId = 1L;

        Mockito.doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isOk());

        Mockito.verify(userService).delete(userId);
    }
}

