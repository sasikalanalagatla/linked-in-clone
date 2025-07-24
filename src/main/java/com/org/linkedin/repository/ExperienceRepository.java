package com.org.linkedin.repository;

import com.org.linkedin.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience,Long> {
    List<Experience> findByUserUserId(Long userId);
}
