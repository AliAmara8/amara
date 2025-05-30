package com.ali.amara.profile;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class ProfileDTO {
    private Long id;
    private String profilePictureUrl;
    private String coverPictureUrl;
    private String civilStatus;
    private String biography;
    private String interests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String city;
    private String profession;
    private String gender;

    public ProfileDTO() {}

    public ProfileDTO(Profile profile) {
        this.id = profile.getId();
        this.profilePictureUrl = profile.getProfilePictureUrl();
        this.coverPictureUrl = profile.getCoverPictureUrl();
        this.civilStatus = profile.getCivilStatus();
        this.biography = profile.getBiography();
        this.interests = profile.getInterests();
        this.birthDate = profile.getBirthDate();
        this.city = profile.getCity();
        this.profession = profile.getProfession();
        this.gender = profile.getGender();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public String getCoverPictureUrl() { return coverPictureUrl; }
    public void setCoverPictureUrl(String coverPictureUrl) { this.coverPictureUrl = coverPictureUrl; }
    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String civilStatus) { this.civilStatus = civilStatus; }
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
