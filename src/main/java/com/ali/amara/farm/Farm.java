package com.ali.amara.farm;


import com.ali.amara.profile.Profile;
import jakarta.persistence.*;

@Entity
@Table(name = "farms")
public class Farm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column
    private String farmType;

    @Column
    private String farmSize;

    @Column
    private String location;

    @Column
    private Integer experienceYears;

    @Column
    private String certifications;

    @Column(length = 2000)
    private String farmDescription;

    @Column
    private String cropTypes;

    @Column
    private String equipment;

    @Column
    private String professionalAssociation;

    @Column
    private String productionVolume;

    @Column
    private String landOwnership;

    @Column
    private String sustainabilityPractices;

    @Column
    private String marketType;

    @Column
    private String weatherRegion;

    // Constructeurs
    public Farm() {}
    public Farm(Profile profile) {
        this.profile = profile;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
    public String getFarmType() { return farmType; }
    public void setFarmType(String farmType) { this.farmType = farmType; }
    public String getFarmSize() { return farmSize; }
    public void setFarmSize(String farmSize) { this.farmSize = farmSize; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    public String getFarmDescription() { return farmDescription; }
    public void setFarmDescription(String farmDescription) { this.farmDescription = farmDescription; }
    public String getCropTypes() { return cropTypes; }
    public void setCropTypes(String cropTypes) { this.cropTypes = cropTypes; }
    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public String getProfessionalAssociation() { return professionalAssociation; }
    public void setProfessionalAssociation(String professionalAssociation) { this.professionalAssociation = professionalAssociation; }
    public String getProductionVolume() { return productionVolume; }
    public void setProductionVolume(String productionVolume) { this.productionVolume = productionVolume; }
    public String getLandOwnership() { return landOwnership; }
    public void setLandOwnership(String landOwnership) { this.landOwnership = landOwnership; }
    public String getSustainabilityPractices() { return sustainabilityPractices; }
    public void setSustainabilityPractices(String sustainabilityPractices) { this.sustainabilityPractices = sustainabilityPractices; }
    public String getMarketType() { return marketType; }
    public void setMarketType(String marketType) { this.marketType = marketType; }
    public String getWeatherRegion() { return weatherRegion; }
    public void setWeatherRegion(String weatherRegion) { this.weatherRegion = weatherRegion; }
}