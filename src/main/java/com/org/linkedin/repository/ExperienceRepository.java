package com.org.linkedin.repository;

import com.org.linkedin.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience,Long> {
    List<Experience> findByUserUserId(Long userId);
}
