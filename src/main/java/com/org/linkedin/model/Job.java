package com.org.linkedin.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

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

    private String jobTypes;

    private String jobWorkPlaceTypes;

    private String recruiterEmail;

    private String experienceLevel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime jobCreatedAt;

    private boolean jobPostEdited;

    @OneToMany(mappedBy = "job", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ApplyJob> applyJobList = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> requiredSkills;

    @Transient
    private Long applicationsCount;

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

    public String getJobTypes() {
        return jobTypes;
    }

    public void setJobTypes(String jobTypes) {
        this.jobTypes = jobTypes;
    }

    public String getJobWorkPlaceTypes() {
        return jobWorkPlaceTypes;
    }

    public void setJobWorkPlaceTypes(String jobWorkPlaceTypes) {
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