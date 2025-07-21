package com.org.linkedin.repository;

import com.org.linkedin.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserUserId(Long userId);
}
