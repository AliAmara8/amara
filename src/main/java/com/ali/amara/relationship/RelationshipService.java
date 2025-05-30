package com.ali.amara.relationship;

import com.ali.amara.notification.NotificationService;
import com.ali.amara.user.UserDTO;
import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RelationshipService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RelationshipRepository relationshipRepository;
    @Autowired
    private NotificationService notificationService;

    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(RelationshipService.class);
    @Autowired
    public RelationshipService(RelationshipRepository relationshipRepository, ModelMapper modelMapper) {
        this.relationshipRepository = relationshipRepository;
        this.modelMapper = modelMapper;
    }
    // Envoyer une demande d'ami
    public RelationshipDTO sendFriendRequest(Long userId, Long friendId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        UserEntity friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Ami non trouvé"));

        Optional<Relationship> existing = relationshipRepository.findByUserAndFriend(user, friend);
        if (existing.isPresent() && (existing.get().getStatus() == RelationshipStatus.PENDING ||
                existing.get().getStatus() == RelationshipStatus.ACCEPTED)) {
            throw new RuntimeException("Une relation active existe déjà");
        }

        Relationship relationship = existing.orElseGet(() -> new Relationship());
        relationship.setUser(user);
        relationship.setFriend(friend);
        relationship.setStatus(RelationshipStatus.PENDING);
        relationship.setCreatedAt(LocalDateTime.now());

        Relationship saved = relationshipRepository.save(relationship);
        notificationService.sendNotification(friendId, user.getLastName() + " vous a envoyé une demande d'amitié");
        return convertToDTO(saved);
    }

    // Accepter une demande
    public RelationshipDTO acceptFriendRequest(Long userId, Long friendId) {
        // Vérifier que les utilisateurs existent
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        UserEntity friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Ami non trouvé"));

        // Trouver la relation entre les deux utilisateurs
        Optional<Relationship> existingRelationship = relationshipRepository.findByUserAndFriend(user, friend);

        // Vérifier que la relation existe et qu'elle est en statut PENDING
        if (existingRelationship.isEmpty() || existingRelationship.get().getStatus() != RelationshipStatus.PENDING) {
            throw new RuntimeException("Aucune demande d'ami en attente à accepter");
        }

        // Récupérer la relation
        Relationship relationship = existingRelationship.get();

        // Mettre à jour le statut de la relation
        relationship.setStatus(RelationshipStatus.ACCEPTED);
        Relationship saved = relationshipRepository.save(relationship);

        // Envoyer une notification à l'utilisateur qui a envoyé la demande
        notificationService.sendNotification(
                relationship.getUser().getId(),
                relationship.getFriend().getLastName() + " a accepté votre demande d'amitié"
        );

        // Retourner le DTO de la relation mise à jour
        return convertToDTO(saved);
    }

    // supprimer un ami
    public RelationshipDTO removeFriend(Long userId, Long friendId) {
        // Récupérer les utilisateurs
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        UserEntity friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Ami non trouvé"));

        // Chercher une relation ACCEPTED dans un sens ou dans l’autre
        Optional<Relationship> relationshipOpt = relationshipRepository.findByUserAndFriendAndStatus(user, friend, RelationshipStatus.ACCEPTED);
        if (!relationshipOpt.isPresent()) {
            logger.info("Aucune relation ACCEPTED dans le sens user->friend. Recherche dans l’autre sens...");
            relationshipOpt = relationshipRepository.findByUserAndFriendAndStatus(friend, user, RelationshipStatus.ACCEPTED);
        }

        // Vérifier si une relation ACCEPTED existe
        Relationship relationship = relationshipOpt
                .orElseThrow(() -> {
                    logger.error("Aucune relation d’amitié acceptée trouvée entre userId={} et friendId={}", userId, friendId);
                    return new RuntimeException("Aucune relation d’amitié acceptée trouvée entre userId=" + userId + " et friendId=" + friendId);
                });

        // Convertir en DTO avant suppression (comme dans rejectFriendRequest)
        RelationshipDTO dto = convertToDTO(relationship);

        // Supprimer la relation
        logger.info("Suppression de la relation entre userId={} et friendId={}", userId, friendId);
        relationshipRepository.delete(relationship);

        // Envoyer une notification à l’autre utilisateur
        Long notifiedUserId = (userId.equals(relationship.getUser().getId())) ? friendId : userId;
        String notifierName = (userId.equals(relationship.getUser().getId())) ? friend.getLastName() : user.getLastName();
        notificationService.sendNotification(notifiedUserId, notifierName + " a supprimé votre amitié");

        // Retourner le DTO
        return dto;
    }
    // blocker un utilisateur
    public RelationshipDTO blockUser(Long userId, Long friendId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        UserEntity friend = userRepository.findById(friendId).orElseThrow(() -> new RuntimeException("Ami non trouvé"));
        Relationship relationship = relationshipRepository.findByUserAndFriend(user, friend)
                .orElseGet(() -> {
                    Relationship newRel = new Relationship();
                    newRel.setUser(user);
                    newRel.setFriend(friend);
                    return newRel;
                });
        relationship.setStatus(RelationshipStatus.BLOCKED);
        return convertToDTO(relationshipRepository.save(relationship));
    }

    public RelationshipDTO rejectFriendRequest(Long userId, Long friendId) {
        // Vérifier que les utilisateurs existent
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        UserEntity friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Ami non trouvé"));

        // Trouver la relation entre les deux utilisateurs
        Optional<Relationship> existingRelationship = relationshipRepository.findByUserAndFriend(user, friend);

        // Vérifier que la relation existe et qu'elle est en statut PENDING
        if (existingRelationship.isEmpty() || existingRelationship.get().getStatus() != RelationshipStatus.PENDING) {
            throw new RuntimeException("Aucune demande d'ami en attente à rejeter");
        }

        // Récupérer la relation
        Relationship relationship = existingRelationship.get();

        // Préparer le DTO avant suppression
        RelationshipDTO dto = convertToDTO(relationship);

        // Supprimer la relation de la base
        logger.info("Suppression de la demande d’amitié entre userId={} et friendId={}", userId, friendId);
        relationshipRepository.delete(relationship);

        // Envoyer une notification à l'utilisateur qui a envoyé la demande
        notificationService.sendNotification(
                relationship.getUser().getId(),
                "Votre demande d'amitié a été rejetée par " + relationship.getFriend().getLastName()
        );

        // Retourner le DTO de la relation supprimée
        return dto;
    }

    // Liste des amis
    public List<UserDTO> getFriends(Long userId) {
        List<Relationship> friendships = relationshipRepository.findAllFriendshipsByUserId(userId);

        return friendships.stream()
                .map(relationship -> {
                    // Si l'utilisateur est "user", l'ami est "friend", et vice-versa
                    return relationship.getUser().getId().equals(userId)
                            ? relationship.getFriend()
                            : relationship.getUser();
                })
                .map(friend -> new UserDTO(
                        friend.getId(),
                        friend.getLastName(),
                        friend.getFirstName(),
                        friend.getEmail()
                ))
                .collect(Collectors.toList());
    }

    // Liste des demandes en attente
    public List<UserDTO> getPendingRequests(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return relationshipRepository.findByUserAndStatus(user, RelationshipStatus.PENDING)
                .stream()
                .map(Relationship::getFriend)
                .map(friend -> new UserDTO(friend.getId(), friend.getLastName(), friend.getFirstName(), friend.getEmail()))
                .collect(Collectors.toList());
    }

    public List<UserDTO> suggestUsers(Long userId) {
        // Récupérer les amis de l'utilisateur
        List<UserDTO> friends = getFriends(userId);

        // Récupérer les utilisateurs suggérés (en tant qu'entités UserEntity)
        List<UserEntity> suggestedUsersEntities = relationshipRepository.findUsersNotFriends(userId);

        // Convertir les UserEntity en UserDTO avec ModelMapper
        List<UserDTO> suggestedUsers = suggestedUsersEntities.stream()
                .map(userEntity -> modelMapper.map(userEntity, UserDTO.class))
                .toList();

        // Filtrer les utilisateurs qui ne sont pas déjà amis avec l'utilisateur spécifié
        return suggestedUsers.stream()
                .filter(user -> !friends.contains(user) && !user.getId().equals(userId))  // Ne pas inclure l'utilisateur lui-même
                .collect(Collectors.toList());
    }




    // Méthode utilitaire pour convertir en DTO
    private RelationshipDTO convertToDTO(Relationship relationship) {
        RelationshipDTO dto = new RelationshipDTO();
        dto.setId(relationship.getId());
        dto.setUser(new UserDTO(
                relationship.getUser().getId(),
                relationship.getUser().getLastName(),
                relationship.getUser().getFirstName(),
                relationship.getUser().getEmail()
        ));
        dto.setFriend(new UserDTO(
                relationship.getFriend().getId(),
                relationship.getFriend().getLastName(),
                relationship.getFriend().getFirstName(),
                relationship.getFriend().getEmail()
        ));
        dto.setStatus(relationship.getStatus().name());
        dto.setCreatedAt(relationship.getCreatedAt());
        return dto;
    }
}