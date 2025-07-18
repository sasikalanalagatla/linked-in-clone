package com.org.linkedin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    private String profilePictureUrl;

    private String fullName;

    private String additionalName;

    private String pronouns;

    private String headline;

    private String industry;

    private String location;

    private String city;

    private Long phoneNumber;

    private String birthday;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Education> educations;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Experience> experiences;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Project> projects;

    @ManyToMany
    private List<Skill> skills;

    @OneToOne(cascade = CascadeType.ALL)
    private AboutUser aboutUser;

}
