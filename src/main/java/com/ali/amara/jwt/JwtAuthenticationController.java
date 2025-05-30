package com.ali.amara.jwt;


import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    // Token blacklist to store invalidated tokens
    private final Set<String> tokenBlacklist = new HashSet<>();

    public JwtAuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            System.out.println("Échec de l'authentification pour " + username);
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String jwt = jwtUtil.generateToken(userDetails);
        System.out.println("hello"+username);
        return ResponseEntity.ok(Map.of("token", jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserEntity user) {
        Optional<UserEntity> existingUser = userService.findByEmailOrPhone(user.getEmail(), user.getPhone());

        System.out.println("User found: " + existingUser.isPresent());

        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Email or phone already in use!");
        }
        // Attribuer un rôle par défaut (ROLE_USER)
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        UserEntity savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // Extract the token from the Authorization header
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
            tokenBlacklist.add(token); // Add the token to the blacklist
            System.out.println("Token invalidated: " + token);
            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }

    // Method to check if a token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
}

