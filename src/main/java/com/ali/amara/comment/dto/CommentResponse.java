package com.ali.amara.comment.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long id,
        String content,
        String imageUrl,
        Long postId,
        Long parentId,
        Long userId,
        String userEmail,
        String userFullName,
        String userProfilePicture,
        int likesCount,
        int repliesCount,
        boolean liked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    @Builder
    public CommentResponse {}
}