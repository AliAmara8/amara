package com.ali.amara.profile.entity;


import com.ali.amara.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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


    public Profile(User user) {
        this.user = user;
    }

}
