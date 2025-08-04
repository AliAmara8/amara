package com.ali.amara.comment.dto;

public record CommentRequest(
        String content,
        Long postId,
        Long parentId,
        String imageUrl
) {
    public CommentRequest withImageUrl(String newImageUrl) {
        return new CommentRequest(content, postId, parentId, newImageUrl);
    }
}