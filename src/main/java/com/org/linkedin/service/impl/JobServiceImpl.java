package com.org.linkedin.service.impl;

import com.org.linkedin.model.Job;
import com.org.linkedin.repository.JobRepository;
import com.org.linkedin.service.JobService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    @Override
    public List<Job> searchJobsByTitleOrCompany(String keyword) {
        return jobRepository.findByJobTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(keyword, keyword);
    }
}