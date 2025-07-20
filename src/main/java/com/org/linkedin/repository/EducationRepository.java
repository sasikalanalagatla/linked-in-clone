package com.org.linkedin.repository;

import com.org.linkedin.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByUserUserId(Long userId);
}
