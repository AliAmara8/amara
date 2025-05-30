package com.ali.amara.user;

import com.ali.amara.post.Post;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity saveUser(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hachage du mot de passe
        return userRepository.save(user);
    }

    public Optional<UserEntity> findByEmailOrPhone(String email, String phone) {
        return userRepository.findByEmailOrPhone(email, phone);
    }

    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public UserDTO getCurrentUser(String identifier) {
        // Rechercher l'utilisateur par e-mail ou téléphone
        UserEntity userEntity = userRepository.findByEmailOrPhone(identifier, identifier)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return convertToDTO(userEntity); // Convertir l'entité en DTO
    }

    private UserDTO convertToDTO(UserEntity user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setLastname(user.getLastName());
        dto.setName(user.getFirstName());
        dto.setEmail(user.getEmail());
        // Ne pas inclure relationships ici, ou mapper uniquement les IDs si nécessaire
        return dto;
    }

}