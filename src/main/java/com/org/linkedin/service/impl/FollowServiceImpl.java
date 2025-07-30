package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.FollowService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;

    public FollowServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void followUser(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            throw new CustomException("INVALID_USER_ID", "Follower or following ID cannot be null");
        }
        Optional<User> follower = userRepository.findById(followerId);
        if (follower.isEmpty()) {
            throw new CustomException("USER_NOT_FOUND", "Follower with ID " + followerId + " not found");
        }
        Optional<User> following = userRepository.findById(followingId);
        if (following.isEmpty()) {
            throw new CustomException("USER_NOT_FOUND", "User to follow with ID " + followingId + " not found");
        }

        if (!follower.get().getFollowing().contains(following.get())) {
            follower.get().getFollowing().add(following.get());
            userRepository.save(follower.get());
        }
    }
}
