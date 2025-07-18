package com.org.linkedin.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long educationId;

    private String schholName;

    private String degree;

    private String fieldOfStudy;

    private String startDate;

    private String endDate;

    private String grade;

    private String extraCurricularActivity;

    private String skills;
}
