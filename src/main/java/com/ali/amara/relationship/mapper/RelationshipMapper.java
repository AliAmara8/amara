package com.ali.amara.relationship.mapper;

import com.ali.amara.relationship.dto.*;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.handler.RelationshipHandlerFactory;
import com.ali.amara.relationship.repository.RelationshipRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        // Déclarez ici les beans Spring dont ce mapper a besoin.
        // Spring les injectera dans la classe générée par MapStruct.
        uses = {RelationshipRepository.class, RelationshipHandlerFactory.class}
)
public abstract class RelationshipMapper {

    // MapStruct utilisera l'injection par constructeur dans la classe générée.
    // Il est donc préférable de ne pas utiliser @Autowired sur les champs ici.
    // Pour que cela fonctionne, MapStruct a besoin d'un moyen de les obtenir,
    // on utilise donc des setters annotés avec @Autowired, ce qui est une
    // méthode d'injection valide que MapStruct et Spring comprennent.
    private RelationshipHandlerFactory handlerFactory;
    private RelationshipRepository relationshipRepository;

    @Autowired
    public void setHandlerFactory(RelationshipHandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    @Autowired
    public void setRepository(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
    }

    /**
     * Méthode principale de mapping de l'entité vers le DTO.
     * Les champs communs sont mappés ici.
     */
    @Mapping(source = "follower.id", target = "followerId")
    @Mapping(source = "follower.firstName", target = "followerName") // Suppose que vous concaténez le nom ailleurs ou que ce champ suffit
    @Mapping(source = "follower.profilePictureUrl", target = "followerProfilePicture")
    @Mapping(source = "following.id", target = "followingId")
    @Mapping(source = "following.firstName", target = "followingName")
    @Mapping(source = "following.profilePictureUrl", target = "followingProfilePicture")
    @Mapping(target = "mutualConnections", expression = "java(relationshipRepository.countMutualConnections(relationship.getFollower().getId(), relationship.getFollowing().getId()))")
    public abstract RelationshipDTO toDto(Relationship relationship);

    /**
     * Cette méthode est appelée par MapStruct après le mapping de base.
     * C'est l'endroit idéal pour appeler nos handlers de stratégie pour les champs spécifiques.
     */
    @AfterMapping
    protected void mapTypeSpecificDetails(Relationship relationship, @MappingTarget RelationshipDTO dto) {
        // Le handlerFactory a été injecté par Spring grâce au setter ci-dessus.
        handlerFactory.getHandler(relationship.getType()).mapTypeSpecificDetailsToDTO(dto, relationship);
    }

    /**
     * Cette "ObjectFactory" indique à MapStruct comment créer l'instance du bon sous-type de DTO
     * en fonction du type de la relation. C'est le cœur du mapping polymorphique.
     */
    @ObjectFactory
    protected RelationshipDTO createDto(Relationship relationship) {
        switch (relationship.getType()) {
            case EXPERT_CONNECT: return new ExpertConnectDTO();
            case EQUIPMENT_SHARE: return new EquipmentShareDTO();
            case FARMING_PARTNER: return new FarmingPartnerDTO();
            case FOLLOW: return new FollowDTO();
            case MENTOR: return new MentorDTO();
            case NEIGHBOR: return new NeighborDTO();
            case SUPPLIER: return new SupplierDTO();
            case BUYER: return new BuyerDTO();
            case COOPERATIVE: return new CooperativeDTO();
            case RESEARCH_PARTNER: return new ResearchPartnerDTO();
            // Le cas default est maintenant robuste et prévient les erreurs de programmation silencieuses.
            default:
                throw new IllegalArgumentException("Unhandled relationship type for DTO creation: " + relationship.getType());
        }
    }
}