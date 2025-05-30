package com.ali.amara.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.base-url}")
    private String baseUrl;

    public String storeFile(MultipartFile file, String subDirectory) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Vérifier le type de fichier (uniquement des images)
            if (!file.getContentType().startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed.");
            }

            // Vérifier la taille du fichier (par exemple, 5 Mo max)
            long maxFileSize = 5 * 1024 * 1024; // 5 Mo
            if (file.getSize() > maxFileSize) {
                throw new RuntimeException("File size exceeds the limit.");
            }

            // Créer le chemin du sous-dossier
            Path subDirectoryPath = Paths.get(uploadDir, subDirectory);

            // Créer le sous-dossier s'il n'existe pas
            if (!Files.exists(subDirectoryPath)) {
                Files.createDirectories(subDirectoryPath);
            }

            // Générer un nom de fichier unique
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Copier le fichier dans le sous-dossier
            Path filePath = subDirectoryPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // Renvoyer l'URL du fichier
            return baseUrl + subDirectory + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    public void deleteFile(String fileName, String subDirectory) {
        try {
            Path filePath = Paths.get(uploadDir, subDirectory, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file.", e);
        }
    }
}