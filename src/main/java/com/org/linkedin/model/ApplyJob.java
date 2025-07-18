package com.org.linkedin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ApplyJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applyJobId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String emailId;

    private Long mobileNumber;

    private String additionalQuestions;

    private String resume;
}