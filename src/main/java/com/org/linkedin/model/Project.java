package com.org.linkedin.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String name;

    private String description;

    @ManyToMany
    private List<Skill> skills;
}
