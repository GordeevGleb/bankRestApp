package org.example.bank_app.service;


import org.example.bank_app.dto.UpdateUserDto;
import org.example.bank_app.dto.UserDto;
import org.example.bank_app.entity.User;

public interface UserService {

    User findByLogin(String login);

    User findById(Long id);

    UserDto update(Long id, UpdateUserDto updateUserDto);

    void delete(Long id);
}
