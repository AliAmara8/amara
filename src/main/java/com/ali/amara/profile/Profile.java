package com.ali.amara.profile;


import com.ali.amara.farm.Farm;
import com.ali.amara.user.UserEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column
    private String profilePictureUrl;

    @Column
    private String coverPictureUrl;

    @Column
    private String civilStatus;

    @Column(length = 1000)
    private String biography;

    @Column
    private String interests;

    @Column
    private LocalDate birthDate;

    @Column
    private String city;

    @Column
    private String profession;

    @Column
    private String gender;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Farm farm;

    // Constructeurs
    public Profile() {}
    public Profile(UserEntity user) {
        this.user = user;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
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
    public Farm getFarm() { return farm; }
    public void setFarm(Farm farm) { this.farm = farm; }
}
