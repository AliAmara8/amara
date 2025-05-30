package com.ali.amara.notification;


import com.ali.amara.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(UserEntity user);
    List<Notification> findByUserAndIsRead(UserEntity user, boolean isRead);
}
