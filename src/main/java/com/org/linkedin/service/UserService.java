package com.org.linkedin.service;

import com.org.linkedin.model.Certification;
import com.org.linkedin.model.User;
import java.util.List;

public interface UserService {
    User getUserById(Long userId);
    User findByEmail(String email);
    User updateUser(User updatedUser);
    void followUser(Long followerId, Long followeeId);
    List<User> getFollowers(User user);
    List<User> getFollowing(User user);
    List<Certification> getUserCertifications(Long userId);
    Certification addCertification(Long userId, Certification certification);
    Certification updateCertification(Certification certification);
    void deleteCertification(Long certificationId);
}