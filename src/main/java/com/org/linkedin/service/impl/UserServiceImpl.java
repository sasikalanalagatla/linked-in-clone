package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.CertificationService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CertificationService certificationService;

    public UserServiceImpl(UserRepository userRepository, CertificationService certificationService) {
        this.userRepository = userRepository;
        this.certificationService = certificationService;
    }

    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User with ID " + userId + " not found"));
    }

    @Override
    public User findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new CustomException("INVALID_EMAIL", "Email cannot be null or empty");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User with email " + email + " not found"));
    }

    @Override
    public void updateUser(User updatedUser) {
        if (updatedUser == null || updatedUser.getUserId() == null) {
            throw new CustomException("INVALID_USER", "User data or ID cannot be null");
        }
        User existingUser = userRepository.findById(updatedUser.getUserId())
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User with ID " + updatedUser.getUserId() + " not found"));

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setHeadline(updatedUser.getHeadline());
        existingUser.setLocation(updatedUser.getLocation());
        existingUser.setCity(updatedUser.getCity());
        existingUser.setIndustry(updatedUser.getIndustry());
        existingUser.setPronouns(updatedUser.getPronouns());
        existingUser.setAbout(updatedUser.getAbout());
        existingUser.setProfilePictureUrl(updatedUser.getProfilePictureUrl());

        userRepository.save(existingUser);
    }

    @Override
    public List<User> getFollowers(User user) {
        if (user == null) {
            throw new CustomException("INVALID_USER", "User cannot be null");
        }
        List<User> followers = user.getFollowers();
        return followers != null ? followers : new ArrayList<>();
    }

    @Override
    public List<User> getFollowing(User user) {
        if (user == null) {
            throw new CustomException("INVALID_USER", "User cannot be null");
        }
        List<User> following = user.getFollowing();
        return following != null ? following : new ArrayList<>();
    }

    @Override
    public List<User> searchByName(String query) {
        return userRepository.findByFullNameContainingIgnoreCase(query);
    }
}