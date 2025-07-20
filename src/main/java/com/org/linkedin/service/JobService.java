package com.org.linkedin.service;

import com.org.linkedin.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {

    Job createJob(Job job);

    Job getJobById(Long jobId);

    Job editJobById(Long jobId);

    List<Job> getAllJobs();

    String deleteJobById(Long jobId);

    Page<Job> getAllJobs(Pageable pageable);

    void updateJob(Job job);

    Page<Job> searchJobs(String keyword, Pageable pageable);
}