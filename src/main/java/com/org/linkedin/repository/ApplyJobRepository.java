package com.org.linkedin.repository;

import com.org.linkedin.model.ApplyJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ApplyJobRepository extends JpaRepository<ApplyJob, Long> {
    @Query("SELECT aj.job.id FROM ApplyJob aj WHERE aj.user.userId = :userId")
    Set<Long> findAppliedJobIdsByUserId(@Param("userId") Long userId);
}
