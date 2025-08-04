package com.ali.amara.session.mapper;

import com.ali.amara.session.dto.UserSessionDTO;
import com.ali.amara.session.entity.UserSession;
import org.springframework.stereotype.Component;

@Component
public class UserSessionMapper {

    public UserSessionDTO toDto(UserSession session) {
        if (session == null) {
            return null;
        }
        return UserSessionDTO.builder()
                .id(session.getId())
                .userId(session.getUser().getId())
                .ipAddress(session.getIpAddress())
                .userAgent(session.getUserAgent())
                .loginTime(session.getLoginTime())
                .active(session.isActive())
                .build();
    }
}