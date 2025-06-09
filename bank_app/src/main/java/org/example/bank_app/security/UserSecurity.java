package org.example.bank_app.security;

import lombok.RequiredArgsConstructor;
import org.example.bank_app.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    public Boolean isBlocked(String login) {
        return userRepository.existsByLoginAndIsBlocked(login, false);
    }
}
