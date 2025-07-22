package com.org.linkedin.service.impl;

import com.org.linkedin.model.Certification;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.CertificationService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Service;

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
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public User updateUser(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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
        if (followerId.equals(followeeId)) return;

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));

        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new IllegalArgumentException("User to follow not found"));

        if (!followee.getFollowers().contains(follower)) {
            followee.getFollowers().add(follower);
            userRepository.save(followee);
        }
    }

    @Override
    public List<Certification> getUserCertifications(Long userId) {
        return certificationService.getCertificationsByUserId(userId);
    }

    @Override
    public Certification addCertification(Long userId, Certification certification) {
        User user = getUserById(userId);
        certification.setUser(user);
        return certificationService.saveCertification(certification);
    }

    @Override
    public Certification updateCertification(Certification certification) {
        return certificationService.saveCertification(certification);
    }

    @Override
    public void deleteCertification(Long certificationId) {
        certificationService.deleteCertification(certificationId);
    }
}