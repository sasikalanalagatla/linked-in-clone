package com.org.linkedin.repository;

import com.org.linkedin.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT DISTINCT j FROM Job j LEFT JOIN j.requiredSkills s " +
            "WHERE LOWER(j.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(j.company.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(j.jobLocation) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.skillName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Job> searchJobsByTitleOrSkill(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.jobCreatedAt >= :createdAfter")
    Page<Job> filterByCreatedAt(@Param("createdAfter") LocalDateTime createdAfter, Pageable pageable);

    @Query("SELECT DISTINCT j FROM Job j LEFT JOIN j.requiredSkills s " +
            "WHERE (LOWER(j.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(j.company.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.skillName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND j.jobCreatedAt >= :createdAfter")
    Page<Job> filterAndSearch(@Param("keyword") String keyword,
                              @Param("createdAfter") LocalDateTime createdAfter,
                              Pageable pageable);

    Page<Job> findByUserUserId(Long userId, Pageable pageable);

    List<Job> findByCompanyId(Long companyId);

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.applyJobList aj LEFT JOIN FETCH aj.user WHERE j.id = :id")
    Optional<Job> findByIdWithApplicants(@Param("id") Long id);
}