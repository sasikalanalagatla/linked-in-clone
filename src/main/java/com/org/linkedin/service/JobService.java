package com.org.linkedin.service;

import com.org.linkedin.model.Job;

import java.util.List;

public interface JobService {

     Job createJob(Job job);

     Job getJobById(Long jobId);

     Job editJobById(Long jobId);

    List<Job> getAllJobs();

    String deleteJobById(Long jobId);

    List<Job> searchJobsByTitleOrCompany(String keyword);

}
