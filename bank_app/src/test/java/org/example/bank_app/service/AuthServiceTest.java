package org.example.bank_app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Set;
import org.example.bank_app.dto.AuthRequest;
import org.example.bank_app.dto.AuthResponse;
import org.example.bank_app.dto.RegisterRequest;
import org.example.bank_app.entity.User;
import org.example.bank_app.entity.enums.Roles;
import org.example.bank_app.exception.ConcurrentException;
import org.example.bank_app.mapper.UserMapper;
import org.example.bank_app.repository.UserRepository;
import org.example.bank_app.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_validCredentials_shouldReturnAuthResponseWithToken() {
        String login = "user";
        String password = "pass";
        String token = "jwt-token";
        AuthRequest request = AuthRequest.builder().login(login).password(password).build();
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn(token);
        AuthResponse response = authService.login(request);
        assertNotNull(response);
        assertEquals(token, response.getToken());


        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authManager).authenticate(captor.capture());
        UsernamePasswordAuthenticationToken authToken = captor.getValue();
        assertEquals(login, authToken.getPrincipal());
        assertEquals(password, authToken.getCredentials());
    }

    @Test
    void login_invalidCredentials_shouldThrowBadCredentialsException() {
        String login = "user";
        String password = "wrongpass";
        AuthRequest request = AuthRequest.builder().login(login).password(password).build();

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil, never()).generateToken(any());
    }

    @Test
    void register_newUser_shouldReturnAuthResponseWithToken() {
        String login = "newUser";
        String rawPassword = "pass";
        String encodedPassword = "encodedPass";
        String token = "jwt-token";

        RegisterRequest request = RegisterRequest.builder()
                .login(login)
                .password(rawPassword)
                .build();

        User user = User.builder()
                .login(login)
                .password(encodedPassword)
                .roles(Set.of(Roles.USER))
                .build();

        when(userRepository.existsByLogin(login)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userMapper.toUser(request, encodedPassword, List.of(Roles.USER), false)).thenReturn(user);
        when(jwtTokenUtil.generateToken(any(UserDetails.class))).thenReturn(token);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(token, response.getToken());

        verify(userRepository).existsByLogin(login);
        verify(passwordEncoder).encode(rawPassword);
        verify(userMapper).toUser(request, encodedPassword, List.of(Roles.USER), false);
        verify(userRepository).save(user);
        verify(jwtTokenUtil).generateToken(any(UserDetails.class));
    }




    @Test
    void register_existingUser_shouldThrowConcurrentException() {
        RegisterRequest request = RegisterRequest.builder()
                .login("existingUser")
                .password("pass")
                .build();

        when(userRepository.existsByLogin(request.getLogin())).thenReturn(true);

        ConcurrentException ex = assertThrows(ConcurrentException.class, () -> authService.register(request));
        assertEquals("user already exists", ex.getMessage());

        verify(userRepository).existsByLogin(request.getLogin());
        verifyNoMoreInteractions(userRepository, passwordEncoder, userMapper, jwtTokenUtil);
    }

    @Test
    void register_userRepositoryThrowsException_shouldPropagate() {
        RegisterRequest request = RegisterRequest.builder().login("user").password("pass").build();
        String encoded = "encodedPass";

        User user = User.builder().login("user").password(encoded).roles(Set.of(Roles.USER)).build();

        when(userRepository.existsByLogin("user")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn(encoded);
        when(userMapper.toUser(any(), any(), any(), any())).thenReturn(user);
        doThrow(new DataAccessException("DB error") {
        }).when(userRepository).save(user);

        assertThrows(DataAccessException.class, () -> authService.register(request));
    }
}

