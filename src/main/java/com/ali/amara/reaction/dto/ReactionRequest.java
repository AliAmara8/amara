package com.ali.amara.reaction.dto;

import com.ali.amara.reaction.enums.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReactionRequest {
    @NotNull(message = "Reaction type cannot be null")
    private ReactionType type;
}
