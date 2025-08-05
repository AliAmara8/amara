package com.ali.amara.relationship.handler;

import com.ali.amara.relationship.dto.RelationshipDTO;
import com.ali.amara.relationship.entity.Relationship;
import com.ali.amara.relationship.enums.RelationshipType;
import org.springframework.stereotype.Component;

/**
 * Le handler pour le type de relation FOLLOW.
 * L'annotation @Component est CRUCIALE. Sans elle, Spring ne crée pas ce bean
 * et le RelationshipHandlerFactory ne peut pas le trouver, ce qui cause l'erreur
 * "Handler not implemented".
 */
@Component
public class FollowHandler implements RelationshipHandler {

    @Override
    public void setTypeSpecificDetails(Relationship relationship, RelationshipDTO details) {
        // Aucune action requise car FOLLOW n'a pas de champs spécifiques.
    }

    @Override
    public void mapTypeSpecificDetailsToDTO(RelationshipDTO dto, Relationship relationship) {
        // Aucune action requise.
    }

    @Override
    public RelationshipType getHandledType() {
        return RelationshipType.FOLLOW;
    }
}