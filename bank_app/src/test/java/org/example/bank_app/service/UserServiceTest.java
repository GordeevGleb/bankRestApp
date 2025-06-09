package org.example.bank_app.service;

import org.example.bank_app.dto.UpdateUserDto;
import org.example.bank_app.dto.UserDto;
import org.example.bank_app.entity.User;
import org.example.bank_app.exception.NotFoundException;
import org.example.bank_app.mapper.UserMapper;
import org.example.bank_app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findByLogin_userExists_shouldReturnUser() {
        String login = "existingUser";
        User user = new User();
        user.setLogin(login);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        User result = userService.findByLogin(login);

        assertNotNull(result);
        assertEquals(login, result.getLogin());
    }

    @Test
    void findByLogin_userNotFound_shouldThrowNotFoundException() {
        String login = "nonExistingUser";

        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.findByLogin(login));
        assertEquals("User with login nonExistingUser not found", exception.getMessage());
    }

    @Test
    void findById_userExists_shouldReturnUser() {
        Long id = 1L;
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void findById_userNotFound_shouldThrowNotFoundException() {
        Long id = 999L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.findById(id));
        assertEquals("User id 999 not found", exception.getMessage());
    }

    @Test
    void update_existingUser_shouldUpdateUser() {
        Long id = 1L;
        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setLogin("oldLogin");
        existingUser.setName("Old Name");

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setLogin("newLogin");
        updateUserDto.setName("New Name");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userMapper.toUserDto(existingUser)).thenReturn(new UserDto());

        UserDto result = userService.update(id, updateUserDto);

        assertNotNull(result);
        assertEquals("newLogin", existingUser.getLogin());
        assertEquals("New Name", existingUser.getName());
    }

    @Test
    void update_userNotFound_shouldThrowNotFoundException() {
        Long id = 999L;
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setLogin("newLogin");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.update(id, updateUserDto));
        assertEquals("User with id 999 not found", exception.getMessage());
    }

    @Test
    void delete_existingUser_shouldDeleteUser() {
        Long id = 1L;

        doNothing().when(userRepository).deleteById(id);

        assertDoesNotThrow(() -> userService.delete(id));

        verify(userRepository).deleteById(id);
    }


}
