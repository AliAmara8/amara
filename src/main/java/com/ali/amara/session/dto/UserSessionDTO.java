package com.ali.amara.session.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserSessionDTO {
    private Long id;
    private Long userId;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginTime;
    private boolean active;
}