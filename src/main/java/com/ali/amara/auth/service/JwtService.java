package com.ali.amara.auth.service;

import com.ali.amara.auth.config.JwtConfig;
import com.ali.amara.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    // --- Génération de Tokens ---

    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        // Les rôles sont déjà inclus dans les autorités de UserDetails
        // extraClaims. put("roles", user.getAuthorities()...); // Optionnel si vous les voulez en plus.
        return buildToken(extraClaims, user);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        return buildToken(extraClaims, user, jwtConfig.getRefreshTokenExpiration());
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtConfig.expiration());
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuer(jwtConfig.issuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expiration)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }


    // --- Validation de Tokens ---

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            // Vérifie que le nom d'utilisateur correspond ET que le token n'est pas expiré
            // (La vérification de l'expiration est déjà faite dans extractAllClaims, mais une double vérification ne fait pas de mal)
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            // Si une exception est levée par extractUsername (signature invalide, expiré, malformé), le token n'est pas valide.
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            // Si on ne peut pas extraire la date, le token est invalide
            return true;
        }
    }


    // --- Extraction des "Claims" (données du token) ---

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        // Il est plus sûr de récupérer l'ID comme un Long ou Integer
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Cette méthode valide la signature, l'expiration, etc. et lève une exception si le token est invalide.
        // C'est le seul endroit où l'on parse le token.
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generatePasswordResetToken(UserDetails user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("purpose", "password_reset");
        // Utilisation d'une durée de vie plus courte, par exemple 1 heure
        long expirationInMillis = ChronoUnit.HOURS.getDuration().toMillis();
        return buildToken(extraClaims, user, expirationInMillis);
    }

    public boolean isPasswordResetTokenValid(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            boolean isResetToken = "password_reset".equals(claims.get("purpose", String.class));
            return isResetToken && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Password reset token validation failed: {}", e.getMessage());
            return false;
        }
    }


    // --- Méthodes Utilitaires ---

    private SecretKey getSigningKey() {
        // J'ai enlevé les .getBytes() car la clé secrète dans votre yml est déjà une chaîne de caractères.
        // Si elle était encodée en Base64, il faudrait utiliser Decoders.BASE64.decode()
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
    }

    public String generateEmergencyLogoutToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("purpose", "emergency_logout"); // On identifie le but du token

        // Ce token a une durée de vie courte, par exemple 1 heure
        long expirationInMillis = ChronoUnit.HOURS.getDuration().toMillis();

        return buildToken(claims, user, expirationInMillis);
    }

    /**
     * Valide si un token est un token de déconnexion d'urgence valide.
     * Vérifie la signature, l'expiration et le "purpose" spécifique.
     * @param token Le token à vérifier.
     * @return Vrai si le token est valide, sinon faux.
     */
    public boolean isEmergencyLogoutTokenValid(String token) {
        try {
            final Claims claims = extractAllClaims(token); // On utilise la méthode privée ici
            boolean isCorrectPurpose = "emergency_logout".equals(claims.get("purpose", String.class));
            return isCorrectPurpose && !isTokenExpired(token);
        } catch (Exception e) {
            // Toute exception (signature invalide, malformé, expiré) rend le token invalide.
            return false;
        }
    }
}