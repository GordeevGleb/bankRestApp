package org.example.bank_app.service;

import org.example.bank_app.dto.AuthRequest;
import org.example.bank_app.dto.AuthResponse;
import org.example.bank_app.dto.RegisterRequest;

public interface AuthService {

    AuthResponse login(AuthRequest request);
    AuthResponse register(RegisterRequest request);
}
