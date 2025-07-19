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

    public Job createJob(Job job){
        return jobRepository.save(job);
    }
    @Override
    public Job getJobById(Long jobId) {
        return jobRepository.getById(jobId);
    }
    @Override
    public Job editJobById(Long jobId){
        return null;
    }
    @Override
    public List<Job> searchJobsByTitleOrCompany(String keyword) {
        return jobRepository.findByJobTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public String deleteJobById(Long jobId) {
        jobRepository.deleteById(jobId);
        return "post deleted";
    }

    @Override
    public void updateJob(Job job) {
        jobRepository.save(job);
    }
}
