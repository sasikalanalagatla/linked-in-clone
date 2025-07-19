package com.org.linkedin.service;

import com.org.linkedin.model.Job;

import java.util.List;

public interface JobService {

    public Job createJob(Job job);

    List<Job> getAllJobs();

    Job getJobById(Long id);

    List<Job> searchJobsByTitleOrCompany(String keyword); // Optional: for search bar

}
