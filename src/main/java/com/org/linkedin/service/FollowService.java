package com.org.linkedin.service;

import com.org.linkedin.model.User;

import java.util.List;

public interface FollowService {
    void followUser(Long followerId, Long followingId);
    List<User> getFollowers(Long userId);
    List<User> getFollowing(Long userId);
}
