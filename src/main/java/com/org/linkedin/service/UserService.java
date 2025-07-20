package com.org.linkedin.service;

import com.org.linkedin.model.User;

public interface UserService {

    User getUserById(Long userId);
    User updateUser(User user);
}
