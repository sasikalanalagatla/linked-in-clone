package com.org.linkedin.service.impl;

import com.org.linkedin.model.User;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.FollowService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;

    public FollowServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void followUser(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElseThrow();
        User following = userRepository.findById(followingId).orElseThrow();

        if (!follower.getFollowing().contains(following)) {
            follower.getFollowing().add(following);
            userRepository.save(follower);
        }
    }

    @Override
    public List<User> getFollowers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getFollowers();
    }

    @Override
    public List<User> getFollowing(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getFollowing();
    }
}
