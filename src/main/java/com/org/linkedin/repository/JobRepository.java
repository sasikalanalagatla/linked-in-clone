package com.org.linkedin.repository;

import com.org.linkedin.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT DISTINCT j FROM Job j JOIN j.requiredSkills s " +
            "WHERE LOWER(j.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.skillName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Job> searchJobsByTitleOrSkill(@Param("keyword") String keyword, Pageable pageable);
}