package com.ali.amara.auth.config;

import com.ali.amara.auth.exception.InvalidTokenException;
import com.ali.amara.auth.service.JwtService;
import com.ali.amara.auth.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(BEARER_PREFIX.length());
            final String username = jwtService.extractUsername(token);

            log.info("Attempting authentication for user: '{}'", username);
            // Vérifie que le token n'est pas blacklisté (pour le logout)
            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new InvalidTokenException("Token has been blacklisted (logged out)");
            }

            // Si on a un username et que l'utilisateur n'est pas déjà authentifié
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                log.info("Username found in token: '{}'", username);
                log.info("Loading UserDetails for username: '{}'", username);
                // On charge l'utilisateur via le UserDetailsService, pas le JwtService
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                log.info("UserDetails loaded successfully. Username from UserDetails: '{}'", userDetails.getUsername());
                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.warn("JWT Token processing failed: {}", e.getMessage());
            SecurityContextHolder.clearContext(); // Assurez-vous que le contexte est propre
        }

        filterChain.doFilter(request, response);
    }
}
