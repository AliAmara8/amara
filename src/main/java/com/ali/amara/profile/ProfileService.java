package com.ali.amara.profile;

import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    // Créer ou mettre à jour un profil
    @Transactional
    public ProfileDTO saveOrUpdateProfile(Long userId, ProfileDTO profileDTO) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUserId(userId)
                .orElse(new Profile(user));

        profile.setProfilePictureUrl(profileDTO.getProfilePictureUrl());
        profile.setCoverPictureUrl(profileDTO.getCoverPictureUrl());
        profile.setCivilStatus(profileDTO.getCivilStatus());
        profile.setBiography(profileDTO.getBiography());
        profile.setInterests(profileDTO.getInterests());
        profile.setBirthDate(profileDTO.getBirthDate());
        profile.setCity(profileDTO.getCity());
        profile.setProfession(profileDTO.getProfession());
        profile.setGender(profileDTO.getGender());

        Profile savedProfile = profileRepository.save(profile);
        return new ProfileDTO(savedProfile);
    }

    // Récupérer un profil par l'ID de l'utilisateur
    public ProfileDTO getProfileByUserId(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return new ProfileDTO(profile);
    }

    // Supprimer un profil
    @Transactional
    public void deleteProfileByUserId(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profileRepository.delete(profile);
    }

    @Transactional
    public ProfileDTO updateProfilePicture(Long userId, String profilePictureUrl) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setProfilePictureUrl(profilePictureUrl);
        profileRepository.save(profile);
        return new ProfileDTO(profile);
    }

    @Transactional
    public ProfileDTO updateCoverPicture(Long userId, String coverPictureUrl) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setCoverPictureUrl(coverPictureUrl);
        profileRepository.save(profile);
        return new ProfileDTO(profile);
    }
}
