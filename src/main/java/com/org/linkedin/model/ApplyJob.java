package com.org.linkedin.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ApplyJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column
    private String resumeUrl;

    @Column
    private LocalDateTime appliedAt;

    @ElementCollection
    @CollectionTable(name = "apply_job_answers", joinColumns = @JoinColumn(name = "apply_job_id"))
    @Column(name = "answer")
    private List<String> additionalQuestionAnswers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public List<String> getAdditionalQuestionAnswers() {
        return additionalQuestionAnswers;
    }

    public void setAdditionalQuestionAnswers(List<String> additionalQuestionAnswers) {
        this.additionalQuestionAnswers = additionalQuestionAnswers;
    }
}