package com.ali.amara.group.entity;

import com.ali.amara.core.BaseEntity;
import com.ali.amara.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @Size(max = 500)
    private String description;

    @Column(name = "group_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupType type;

    @Column(name = "privacy_level", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PrivacyLevel privacyLevel = PrivacyLevel.PUBLIC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "group_admins",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> admins = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> members = new HashSet<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GroupMembership> memberships = new ArrayList<>();

    private String location;
    private String coverImageUrl;
    private boolean verified;

    // Cooperative specific fields
    private String registrationNumber;
    private String cooperativeType;
    private Integer membershipFee;
    private String bylawsUrl;

    // Research group fields
    private String researchFocus;
    private String institutionAffiliation;
    private String fundingSource;

    // Market group fields
    private String marketType;
    private String tradingRules;
    private String paymentTerms;

    // Equipment sharing group fields
    private String equipmentTypes;
    private String sharingPolicies;
    private String maintenanceSchedule;

    // Learning group fields
    private String curriculum;
    private String expertiseLevel;
    private String meetingSchedule;

    // Event group fields
    private String eventTypes;
    private String eventFrequency;
    private String participationRules;

    // Organic farming fields
    @ElementCollection
    @CollectionTable(name = "group_certifications", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "certification")
    @Builder.Default
    private Set<String> certifications = new HashSet<>();

    // Youth farmers fields
    private String mentorshipProgram;

    public void addAdmin(User user) {
        admins.add(user);
    }

    public void removeAdmin(User user) {
        admins.remove(user);
    }

    public void addMember(User user, MembershipType membershipType) {
        GroupMembership membership = new GroupMembership();
        membership.setGroup(this);
        membership.setUser(user);
        membership.setType(membershipType);
        memberships.add(membership);
        members.add(user);
    }

    public void removeMember(User user) {
        memberships.removeIf(m -> m.getUser().equals(user));
        members.remove(user);
    }

    public boolean isAdmin(User user) {
        return admins.contains(user);
    }

    public boolean isMember(User user) {
        return members.contains(user);
    }
}
