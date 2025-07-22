package com.org.linkedin.service.impl;

import com.org.linkedin.model.User;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.FollowService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;

    public FollowServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void followUser(Long followerId, Long followingId) {
        Optional<User> follower = userRepository.findById(followerId);
        Optional<User> following = userRepository.findById(followingId);

        if (!follower.get().getFollowing().contains(following)) {
            follower.get().getFollowing().add(following.get());
            userRepository.save(follower.get());
        }
    }

    @Override
    public List<User> getFollowers(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.get().getFollowers();
    }

    @Override
    public List<User> getFollowing(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.get().getFollowing();
    }
}
