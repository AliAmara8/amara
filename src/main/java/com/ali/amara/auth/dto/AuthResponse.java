package com.ali.amara.auth.dto;

import com.ali.amara.session.dto.UserSessionDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        UserSessionDTO sessionInfo
) {
    public static AuthResponse of(String accessToken) {
        return new AuthResponse(accessToken, null, null);
    }

    public static AuthResponse withRefresh(String accessToken, String refreshToken) {
        return new AuthResponse(accessToken, refreshToken, null);
    }
    // Constructeur personnalisé pour le cas sans user
    public AuthResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, null); // Appelle le constructeur principal avec user = null
    }
}