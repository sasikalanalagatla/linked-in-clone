package com.org.linkedin.service;

import com.org.linkedin.model.Job;

public interface JobService {

     Job createJob(Job job);

     Job getJobById(Long jobId);

     Job editJobById(Long jobId);
}
