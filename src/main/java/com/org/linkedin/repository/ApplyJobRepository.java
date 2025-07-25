package com.org.linkedin.repository;

import com.org.linkedin.model.ApplyJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ApplyJobRepository extends JpaRepository<ApplyJob, Long> {
    Page<ApplyJob> findByUserUserId(Long userId, Pageable pageable);
    Set<Long> findAppliedJobIdsByUserUserId(Long userId);
    Long countByJobId(Long jobId);
}