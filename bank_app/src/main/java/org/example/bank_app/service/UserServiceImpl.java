package org.example.bank_app.service;

import lombok.RequiredArgsConstructor;
import org.example.bank_app.dto.UpdateUserDto;
import org.example.bank_app.dto.UserDto;
import org.example.bank_app.entity.User;
import org.example.bank_app.exception.NotFoundException;
import org.example.bank_app.mapper.UserMapper;
import org.example.bank_app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(
                () -> new NotFoundException(String.format("User with login %s not found", login))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("User id %d not found", id))
        );
    }

    @Override
    public UserDto update(Long id, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("User with id %d not found", id))
        );
        if (updateUserDto.getLogin() != null) {
            user.setLogin(updateUserDto.getLogin());
        }
        if (updateUserDto.getIsBlocked() != null) {
            user.setIsBlocked(updateUserDto.getIsBlocked());
        }
        if (updateUserDto.getName() != null) {
            user.setName(updateUserDto.getName());
        }
        if (updateUserDto.getRoles() != null) {
            user.setRoles(updateUserDto.getRoles());
        }
        return userMapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
