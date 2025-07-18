package com.org.linkedin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long experienceId;

    private String title;

    private String employmentType;

    private String companyName;

    private String startDate;

    private String endDate;

    private String location;

    private String workType;

    private String description;

    @ManyToMany
    private List<Skill> skills;
}
