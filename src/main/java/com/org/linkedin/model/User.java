package com.org.linkedin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true , nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private List<ApplyJob> appliedJobs;

    @OneToMany(mappedBy = "user")
    private List<JobPost> jobPosts;

    @OneToOne(cascade = CascadeType.ALL)
    private UserProfile userProfile;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private List<User> following;

    @ManyToMany(mappedBy = "following")
    private List<User> followers;
}