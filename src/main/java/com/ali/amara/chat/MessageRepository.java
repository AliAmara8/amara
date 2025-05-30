package com.ali.amara.chat;

import com.ali.amara.chat.Message;
import com.ali.amara.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrReceiverAndSender(UserEntity sender, UserEntity receiver, UserEntity receiver2, UserEntity sender2);
}
