package com.ali.amara.auth.service;

import com.ali.amara.auth.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
    private final UserDetailsService userDetailsService;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuer(jwtConfig.issuer())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.expiration()))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.secret())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(jwtConfig.secret())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtConfig.secret())
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    public UserDetails loadUserByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }
}
