package com.org.linkedin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class AboutUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aboutId;

    private String about;

    @ManyToMany
    private List<Skill> skills;
}
