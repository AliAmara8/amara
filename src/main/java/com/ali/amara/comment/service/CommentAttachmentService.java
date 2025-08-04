package com.ali.amara.comment.service;

import com.ali.amara.config.FileStorageConfig; // <-- Importer la config
import com.ali.amara.comment.dto.CommentRequest;
import com.ali.amara.storage.service.FileStorageService;
import com.ali.amara.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentAttachmentService {

    private final FileStorageService fileStorageService;
    private final FileStorageConfig fileStorageConfig;

    public CommentRequest handleFileUpload(MultipartFile file, User user, CommentRequest request) {
        if (file != null && !file.isEmpty()) {
            try {
                // On construit un chemin dynamique et propre
                // ex: "comments/1" où 1 est l'ID de l'utilisateur
                String subDirectory = fileStorageConfig.getCommentPicDir() + "/" + user.getId();

                // On passe ce sous-dossier au service de stockage
                String fileUrl = fileStorageService.storeFile(file, subDirectory);

                log.debug("File uploaded for comment by user {}: {}", user.getId(), fileUrl);
                return request.withImageUrl(fileUrl);
            } catch (Exception e) {
                log.error("File upload failed for user {}", user.getId(), e);
                throw new RuntimeException("File upload failed", e);
            }
        }
        return request;
    }
}