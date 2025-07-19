package com.org.linkedin.repository;

import com.org.linkedin.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {
    List<Job> findByJobTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(String title, String company);

}
