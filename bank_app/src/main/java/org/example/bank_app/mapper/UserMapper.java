package org.example.bank_app.mapper;

import org.example.bank_app.dto.RegisterRequest;
import org.example.bank_app.dto.UserDto;
import org.example.bank_app.entity.User;
import org.example.bank_app.entity.enums.Roles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "cards", ignore = true)
    @Mapping(source = "password", target = "password")
    @Mapping(source = "roles", target = "roles")
    @Mapping(source = "isBlocked", target = "isBlocked")
    @Mapping(source = "request.login", target = "login")
    @Mapping(source = "request.name", target = "name")
    User toUser(RegisterRequest request, String password, List<Roles> roles, Boolean isBlocked);

    UserDto toUserDto(User user);
}
