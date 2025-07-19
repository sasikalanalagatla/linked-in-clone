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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public WorkPlaceType getJobWorkPlaceTypes() {
        return jobWorkPlaceTypes;
    }

    public void setJobWorkPlaceTypes(WorkPlaceType jobWorkPlaceTypes) {
        this.jobWorkPlaceTypes = jobWorkPlaceTypes;
    }

    public JobType getJobTypes() {
        return jobTypes;
    }

    public void setJobTypes(JobType jobTypes) {
        this.jobTypes = jobTypes;
    }

    public LocalDateTime getJobCreatedAt() {
        return jobCreatedAt;
    }

    public void setJobCreatedAt(LocalDateTime jobCreatedAt) {
        this.jobCreatedAt = jobCreatedAt;
    }

    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public void setRecruiterEmail(String recruiterEmail) {
        this.recruiterEmail = recruiterEmail;
    }

    public boolean isJobPostEdited() {
        return isJobPostEdited;
    }

    public void setJobPostEdited(boolean jobPostEdited) {
        isJobPostEdited = jobPostEdited;
    }

    public Long getApplicationsCount() {
        return applicationsCount;
    }

    public void setApplicationsCount(Long applicationsCount) {
        this.applicationsCount = applicationsCount;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<Skill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<AdditionalQuestion> getAdditionalQuestions() {
        return additionalQuestions;
    }

    public void setAdditionalQuestions(List<AdditionalQuestion> additionalQuestions) {
        this.additionalQuestions = additionalQuestions;
    }
}
