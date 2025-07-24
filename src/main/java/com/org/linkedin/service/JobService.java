package com.org.linkedin.service;

import com.org.linkedin.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface JobService {

    Job createJob(Job job, Principal principal);
    Job getJobById(Long jobId);
    String deleteJobById(Long jobId);
    Page<Job> getAllJobs(Pageable pageable);
    void updateJob(Job job);
    Page<Job> searchJobs(String keyword, Pageable pageable);
}
