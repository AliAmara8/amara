package com.ali.amara.auth.service;

import com.ali.amara.auth.dto.*;
import com.ali.amara.auth.exception.AuthenticationFailedException;
import com.ali.amara.auth.exception.EmailAlreadyExistsException;
import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.email());

        if (userService.existsByEmailOrPhone(request.email(), request.phone())) {
            throw new EmailAlreadyExistsException("Email or phone already in use");
        }

        UserEntity user = userService.createUser(request);
        String token = jwtService.generateToken(user);

        log.info("User registered successfully: {}", user.getEmail());

        return new AuthResponse(
                token,
                jwtService.getExpirationDateFromToken(token),
                userService.toUserResponse(user)
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse authenticate(AuthRequest request) {
        log.info("Authentication attempt for: {}", request.login());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.login(),
                            request.password()
                    )
            );
        } catch (Exception e) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        UserEntity user = userService.findByEmailOrPhone(request.login())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);

        log.info("User authenticated: {}", user.getEmail());

        return new AuthResponse(
                token,
                jwtService.getExpirationDateFromToken(token),
                userService.toUserResponse(user)
        );
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String token = jwtService.extractToken(request);
        tokenBlacklistService.blacklistToken(token);
        log.info("User logged out. Token blacklisted");
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        return userService.findByEmail(username)
                .map(userService::toUserResponse)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}