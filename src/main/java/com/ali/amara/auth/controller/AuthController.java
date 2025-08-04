package com.ali.amara.auth.controller;

import com.ali.amara.auth.dto.*;
import com.ali.amara.auth.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API d'authentification et gestion des utilisateurs")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final HttpServletRequest httpServletRequest;

    @PostMapping("/register")
    @Operation(summary = "Enregistrer un nouvel utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de requête invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Authentifier un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "400", description = "Données de requête invalides")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request,
                                              HttpServletRequest httpServletRequest) {
        AuthResponse response = authenticationService.authenticate(request, httpServletRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Rafraîchir le token d'accès")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token rafraîchi avec succès"),
            @ApiResponse(responseCode = "401", description = "Refresh token invalide ou expiré")
    })
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
        AuthResponse response = authenticationService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnecter l'utilisateur")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Déconnexion réussie"),
            @ApiResponse(responseCode = "401", description = "Token invalide ou expiré")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authenticationService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Obtenir les informations de l'utilisateur courant")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Informations utilisateur récupérées"),
            @ApiResponse(responseCode = "401", description = "Token invalide ou expiré")
    })
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = authenticationService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password-reset/request")
    @Operation(summary = "Demander une réinitialisation de mot de passe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email de réinitialisation envoyé"),
            @ApiResponse(responseCode = "400", description = "Email invalide")
    })
    public ResponseEntity<Void> requestPasswordReset(@RequestParam String email) {
        authenticationService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset/confirm")
    @Operation(summary = "Confirmer la réinitialisation du mot de passe")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Mot de passe réinitialisé avec succès"),
            @ApiResponse(responseCode = "400", description = "Token de réinitialisation invalide ou expiré")
    })
    public ResponseEntity<Void> confirmPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        authenticationService.confirmPasswordReset(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    @Operation(summary = "Changer le mot de passe de l'utilisateur connecté")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Mot de passe changé avec succès"),
            @ApiResponse(responseCode = "400", description = "Ancien mot de passe incorrect"),
            @ApiResponse(responseCode = "401", description = "Token invalide ou expiré")
    })
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid ChangePasswordRequest request) {
        authenticationService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }
}