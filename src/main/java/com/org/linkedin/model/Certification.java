package com.org.linkedin.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String certificationName;

    private String issuingOrganization;

    private LocalDate issueDate;

    private LocalDate expirationDate;

    private String certificationImageUrl;

    public Certification() {}

    public Certification(User user, String certificationName, String issuingOrganization, LocalDate issueDate,
                         LocalDate expirationDate, String certificationImageUrl) {
        this.user = user;
        this.certificationName = certificationName;
        this.issuingOrganization = issuingOrganization;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.certificationImageUrl = certificationImageUrl;
    }

    public Long getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(Long certificationId) {
        this.certificationId = certificationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCertificationName() {
        return certificationName;
    }

    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
    }

    public String getIssuingOrganization() {
        return issuingOrganization;
    }

    public void setIssuingOrganization(String issuingOrganization) {
        this.issuingOrganization = issuingOrganization;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCertificationImageUrl() {
        return certificationImageUrl;
    }

    public void setCertificationImageUrl(String certificationImageUrl) {
        this.certificationImageUrl = certificationImageUrl;
    }
}