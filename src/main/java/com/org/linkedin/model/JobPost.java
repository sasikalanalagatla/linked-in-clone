package com.org.linkedin.model;

import com.org.linkedin.enums.JobType;
import com.org.linkedin.enums.WorkPlaceType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private String company;

    private String jobDescription;

    private String jobLocation;

    @Enumerated(EnumType.STRING)
    private List<WorkPlaceType> jobWorkPlaceTypes;

    @Enumerated(EnumType.STRING)
    private List<JobType> jobTypes;

    private boolean isRemoteAvailable;

    @CreationTimestamp
    private LocalDateTime jobCreatedAt;

    private LocalDateTime applicationDeadline;

    private String recruiterEmail;

    private boolean isJobPostEdited;

    private List<String> requiredSkills;
}
