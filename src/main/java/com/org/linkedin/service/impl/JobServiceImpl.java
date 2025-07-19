package com.org.linkedin.service.impl;

import com.org.linkedin.model.Job;
import com.org.linkedin.repository.JobRepository;
import com.org.linkedin.service.JobService;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job createJob(Job job){
        return jobRepository.save(job);
    }
}
