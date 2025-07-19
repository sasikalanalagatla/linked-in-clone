package com.org.linkedin.model;

import com.org.linkedin.enums.JobType;
import com.org.linkedin.enums.WorkPlaceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Job {

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
    private WorkPlaceType jobWorkPlaceTypes;

    @Enumerated(EnumType.STRING)
    private JobType jobTypes;

    @CreationTimestamp
    private LocalDateTime jobCreatedAt;

    private LocalDateTime applicationDeadline;

    private String recruiterEmail;

    private boolean isJobPostEdited;

    private Long applicationsCount;

    private String experienceLevel;

    private Long companyId;

    @ManyToMany
    private List<Skill> requiredSkills;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdditionalQuestion> additionalQuestions = new ArrayList<>();

}
