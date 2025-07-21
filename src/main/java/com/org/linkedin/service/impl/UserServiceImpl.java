package com.org.linkedin.service.impl;

import com.org.linkedin.model.User;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public User updateUser(User updatedUser) {
        User existingUser = userRepository.findById(2l)
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

}
