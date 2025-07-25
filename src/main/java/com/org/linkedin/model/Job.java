package com.org.linkedin.model;

import com.org.linkedin.enums.JobType;
import com.org.linkedin.enums.WorkPlaceType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jobTitle;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    private String jobLocation;

    @Enumerated(EnumType.STRING)
    private JobType jobTypes;

    @Enumerated(EnumType.STRING)
    private WorkPlaceType jobWorkPlaceTypes;

    private String recruiterEmail;

    @Column
    private String experienceLevel;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplyJob> applyJobList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(updatable = false)
    private LocalDateTime jobCreatedAt;

    @Column
    private boolean jobPostEdited;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdditionalQuestion> additionalQuestions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> requiredSkills;

    @Transient
    private Long applicationsCount;

    @PrePersist
    protected void onCreate() {
        this.jobCreatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public JobType getJobTypes() {
        return jobTypes;
    }

    public void setJobTypes(JobType jobTypes) {
        this.jobTypes = jobTypes;
    }

    public WorkPlaceType getJobWorkPlaceTypes() {
        return jobWorkPlaceTypes;
    }

    public void setJobWorkPlaceTypes(WorkPlaceType jobWorkPlaceTypes) {
        this.jobWorkPlaceTypes = jobWorkPlaceTypes;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public void setRecruiterEmail(String recruiterEmail) {
        this.recruiterEmail = recruiterEmail;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDateTime getJobCreatedAt() {
        return jobCreatedAt;
    }

    public void setJobCreatedAt(LocalDateTime jobCreatedAt) {
        this.jobCreatedAt = jobCreatedAt;
    }

    public boolean isJobPostEdited() {
        return jobPostEdited;
    }

    public void setJobPostEdited(boolean jobPostEdited) {
        this.jobPostEdited = jobPostEdited;
    }

    public List<AdditionalQuestion> getAdditionalQuestions() {
        return additionalQuestions;
    }

    public void setAdditionalQuestions(List<AdditionalQuestion> additionalQuestions) {
        this.additionalQuestions = additionalQuestions;
    }

    public Set<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<Skill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public Long getApplicationsCount() {
        return applicationsCount;
    }

    public void setApplicationsCount(Long applicationsCount) {
        this.applicationsCount = applicationsCount;
    }

    public List<ApplyJob> getApplyJobList() {
        return applyJobList;
    }

    public void setApplyJobList(List<ApplyJob> applyJobList) {
        this.applyJobList = applyJobList;
    }
}