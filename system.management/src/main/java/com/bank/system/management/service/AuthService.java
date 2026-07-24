package com.bank.system.management.service;

import com.bank.system.management.dto.AuthResponse;
import com.bank.system.management.dto.LoginRequest;
import com.bank.system.management.dto.RegisterRequest;
import com.bank.system.management.entity.User;
import com.bank.system.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {

        log.info("Register request received for email: {}", request.getEmail());

        if (repository.existsByEmail(request.getEmail())) {
            log.error("Email already exists: {}", request.getEmail());
            throw new ResourceAlreadyExistsException(
                    "User already exists with email: " + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        repository.save(user);

        log.info("User registered successfully: {}", request.getEmail());
    }

    public AuthResponse login(LoginRequest request) {

        log.info("Login request received for email: {}", request.getEmail());

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        String token = jwtService.generateToken(request.getEmail());

        log.info("JWT token generated successfully for {}", request.getEmail());

        return AuthResponse.builder()
                .token(token)
                .build();
    }

}
