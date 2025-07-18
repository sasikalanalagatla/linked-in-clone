package com.org.linkedin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long educationId;

    private String schoolName;

    private String degree;

    private String fieldOfStudy;

    private String startDate;

    private String endDate;

    private String grade;

    private String extraCurricularActivity;

    @ManyToMany
    private List<Skill> skills;
}
