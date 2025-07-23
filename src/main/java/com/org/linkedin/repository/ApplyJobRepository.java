package com.org.linkedin.repository;

import com.org.linkedin.model.ApplyJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplyJobRepository extends JpaRepository<ApplyJob, Long> {

    boolean existsByJobIdAndUserEmail(Long jobId, String email);
}
