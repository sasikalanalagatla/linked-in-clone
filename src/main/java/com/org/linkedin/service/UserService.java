package com.org.linkedin.service;

import com.org.linkedin.model.User;
import java.util.List;

public interface UserService {
    User getUserById(Long userId);
    User findByEmail(String email);
    void updateUser(User updatedUser);
    List<User> getFollowers(User user);
    List<User> getFollowing(User user);
    public List<User> searchByName(String query);
    }