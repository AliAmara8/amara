package com.ali.amara.session.entity;

import com.ali.amara.core.BaseEntity;
import com.ali.amara.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "last_activity_time")
    private LocalDateTime lastActivityTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive  = true;
}