package com.ali.amara.chat;


import com.ali.amara.chat.MessageDTO;
import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserRepository;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SocketIOServer socketIOServer;

    public MessageDTO sendMessage(Long senderId, Long receiverId, String content, String imageUrl, String fileUrl) {
        UserEntity sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        UserEntity receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        Message message = new Message(sender, receiver, content, imageUrl, fileUrl);
        Message saved = messageRepository.save(message);

        MessageDTO messageDTO = convertToDTO(saved);
        logger.info("Message envoyé : id={}, senderId={}, receiverId={}, content={}",
                messageDTO.getId(), senderId, receiverId, content);

        // Log et envoi Socket.IO
        logger.info("Diffusion Socket.IO au client receiverId={}", receiverId);
        socketIOServer.getBroadcastOperations().sendEvent("chatMessage", messageDTO);

        return messageDTO;
    }

    public List<MessageDTO> getConversation(Long userId1, Long userId2) {
        UserEntity user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("Utilisateur 1 non trouvé"));
        UserEntity user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("Utilisateur 2 non trouvé"));

        List<Message> messages = messageRepository.findBySenderAndReceiverOrReceiverAndSender(user1, user2, user1, user2);
        return messages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private MessageDTO convertToDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getContent(),
                message.getImageUrl(),
                message.getFileUrl(),
                message.getSentAt().toString(),
                message.getStatus().name() // Convertir enum en String
        );
    }
}