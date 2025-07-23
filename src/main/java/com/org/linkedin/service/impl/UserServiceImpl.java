package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Certification;
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
    public User updateUser(User updatedUser) {
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

        return userRepository.save(existingUser);
    }

    @Override
    public void followUser(Long followerId, Long followeeId) {
        if (followerId == null || followeeId == null) {
            throw new CustomException("INVALID_USER_ID", "Follower or followee ID cannot be null");
        }
        if (followerId.equals(followeeId)) {
            throw new CustomException("INVALID_REQUEST", "Cannot follow self");
        }
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "Follower with ID " + followerId + " not found"));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User to follow with ID " + followeeId + " not found"));

        if (!follower.getFollowing().contains(followee)) {
            follower.getFollowing().add(followee);
            userRepository.save(follower);
        }
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
    public List<Certification> getUserCertifications(Long userId) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        return certificationService.getCertificationsByUserId(userId);
    }

    @Override
    public Certification addCertification(Long userId, Certification certification) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        if (certification == null) {
            throw new CustomException("INVALID_CERTIFICATION", "Certification data cannot be null");
        }
        User user = getUserById(userId);
        certification.setUser(user);
        return certificationService.saveCertification(certification);
    }

    @Override
    public Certification updateCertification(Certification certification) {
        if (certification == null || certification.getCertificationId() == null) {
            throw new CustomException("INVALID_CERTIFICATION", "Certification data or ID cannot be null");
        }
        return certificationService.saveCertification(certification);
    }

    @Override
    public void deleteCertification(Long certificationId) {
        if (certificationId == null) {
            throw new CustomException("INVALID_CERTIFICATION_ID", "Certification ID cannot be null");
        }
        certificationService.deleteCertification(certificationId);
    }
}