package org.example.bank_app.service;

import lombok.RequiredArgsConstructor;
import org.example.bank_app.dto.AuthRequest;
import org.example.bank_app.dto.AuthResponse;
import org.example.bank_app.dto.RegisterRequest;
import org.example.bank_app.entity.User;
import org.example.bank_app.entity.enums.Roles;
import org.example.bank_app.exception.ConcurrentException;
import org.example.bank_app.mapper.UserMapper;
import org.example.bank_app.repository.UserRepository;
import org.example.bank_app.util.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(user);
        return AuthResponse.builder().token(token).build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new ConcurrentException("user already exists");
        }

        User user = userMapper.toUser(request,
                passwordEncoder.encode(request.getPassword()),
                List.of(Roles.USER),
                false);
        userRepository.save(user);

        String token = jwtTokenUtil.generateToken(org.springframework.security.core.userdetails.User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList()))

                .build()
        );

        return AuthResponse.builder().token(token).build();
    }
}
