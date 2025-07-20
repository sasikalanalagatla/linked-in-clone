package com.org.linkedin.repository;

import com.org.linkedin.model.User;
import com.org.linkedin.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile,Long> {
    UserProfile findByFullName(String fullName);
}
