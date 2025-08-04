package com.ali.amara.user.service;

import com.ali.amara.auth.dto.RegisterRequest;
import com.ali.amara.core.service.BaseCrudService;
import com.ali.amara.core.util.SecurityUtils;
import com.ali.amara.core.util.ValidationUtils;
import com.ali.amara.user.entity.User;
import com.ali.amara.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService extends BaseCrudService<User, Long> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String uploadDir = "uploads/profile-pictures";

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        // Create upload directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public User createUser(RegisterRequest request) {
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(new HashSet<>())
                .build();
        user.getRoles().add("USER");
        return save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new SecurityException("No authenticated user found"));
        return findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            // New user
            validateNewUser(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.setRoles(new HashSet<>());
                user.getRoles().add("USER");
            }
        } else {
            // Update user
            User existingUser = getEntityById(user.getId());
            SecurityUtils.validateAccess(existingUser.getUsername());

            // Don't update password if not provided
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }

        return super.save(user);
    }

    @Override
    public void deleteById(Long id) {
        User user = getEntityById(id);
        SecurityUtils.validateAccess(user.getUsername());
        super.deleteById(id);
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByProfileSlug(String profileSlug) {
        return userRepository.findByProfileSlug(profileSlug);
    }

    public String uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        User user = getEntityById(userId);
        SecurityUtils.validateAccess(user.getUsername());

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath);

        String fileUrl = "/uploads/profile-pictures/" + fileName;
        user.setProfilePictureUrl(fileUrl);
        save(user);

        return fileUrl;
    }

    public String uploadAvatar(Long userId, MultipartFile file) throws IOException {
        User user = getEntityById(userId);
        SecurityUtils.validateAccess(user.getUsername());

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath);

        String fileUrl = "/uploads/avatar/" + fileName;
        user.setAvatarUrl(fileUrl);
        save(user);

        return fileUrl;
    }

    private void validateNewUser(User user) {
        if (!ValidationUtils.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (user.getPhoneNumber() != null && !ValidationUtils.isValidPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    @Transactional
    public void verifyEmail(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Transactional
    public void verifyPhone(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        user.setPhoneVerified(true);
        userRepository.save(user);
    }

}


