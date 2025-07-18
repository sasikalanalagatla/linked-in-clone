package com.org.linkedin.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobTitle;

    private String company;

    private String jobDescription;

    private String jobLocation;

    private List<String> jobWorkPlaceTypes;

    private List<String> jobTypes;

    private boolean isRemoteAvailable;

    private List<String> requiredSkills;

    private LocalDateTime jobCreatedAt;

    private LocalDateTime applicationDeadline;

    private String recruiterEmail;

    private boolean isJobPostEdited;
}
