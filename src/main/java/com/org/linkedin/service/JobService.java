package com.org.linkedin.service;

import com.org.linkedin.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface JobService {

    Page<Job> getAllJobs(Pageable pageable);

    Page<Job> searchJobs(String keyword, Pageable pageable);

    Job getJobById(Long jobId);

    void createJob(Job job, Principal principal);

    void updateJob(Job updatedJob, Principal principal);

    void deleteJob(Long jobId, Principal principal);
}