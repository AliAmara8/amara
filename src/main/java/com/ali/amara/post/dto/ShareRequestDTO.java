package com.ali.amara.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShareRequestDTO {
    // Peut être null si l'utilisateur partage sans ajouter de commentaire
    private String content;
}