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
    @Query("SELECT a.job.id FROM ApplyJob a WHERE a.user.userId = :userId")
    Set<Long> findAppliedJobIdsByUserUserId(@Param("userId") Long userId);
    Long countByJobId(Long jobId);
}