package com.org.linkedin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ApplyJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applyJobId;

    private String userName;

    private String email;

    private Long mobileNumber;

    private String additionalQuestions;

    private String resumeUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getApplyJobId() {
        return applyJobId;
    }

    public void setApplyJobId(Long applyJobId) {
        this.applyJobId = applyJobId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return email;
    }

    public void setEmailId(String email) {
        this.email = email;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAdditionalQuestions() {
        return additionalQuestions;
    }

    public void setAdditionalQuestions(String additionalQuestions) {
        this.additionalQuestions = additionalQuestions;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}