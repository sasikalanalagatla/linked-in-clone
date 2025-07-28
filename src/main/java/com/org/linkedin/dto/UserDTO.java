package com.org.linkedin.dto;

public class UserDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String profilePictureUrl;


    public UserDTO(Long userId, String fullName, String email, String profilePictureUrl) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
