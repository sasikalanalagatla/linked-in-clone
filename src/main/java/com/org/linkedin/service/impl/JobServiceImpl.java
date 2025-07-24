package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Job;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.JobRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobServiceImpl(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Job createJob(Job job, Principal principal) {
        String email = principal.getName();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        job.setUser(optionalUser.get());
        return jobRepository.save(job);
    }

    @Override
    public Job getJobById(Long jobId) {
        if (jobId == null) {
            throw new CustomException("INVALID_JOB_ID", "Job ID cannot be null");
        }
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException("JOB_NOT_FOUND", "Job with ID " + jobId + " not found"));
    }

    @Override
    public Page<Job> getAllJobs(Pageable pageable) {
        if (pageable == null) {
            throw new CustomException("INVALID_PAGEABLE", "Pageable cannot be null");
        }
        return jobRepository.findAll(pageable);
    }

    @Override
    public Page<Job> searchJobs(String keyword, Pageable pageable) {
        if (keyword == null) {
            throw new CustomException("INVALID_KEYWORD", "Search keyword cannot be null");
        }
        if (pageable == null) {
            throw new CustomException("INVALID_PAGEABLE", "Pageable cannot be null");
        }
        return jobRepository.searchJobsByTitleOrSkill(keyword, pageable);
    }

    @Override
    public String deleteJobById(Long jobId) {
        if (jobId == null) {
            throw new CustomException("INVALID_JOB_ID", "Job ID cannot be null");
        }
        if (!jobRepository.existsById(jobId)) {
            throw new CustomException("JOB_NOT_FOUND", "Job with ID " + jobId + " not found");
        }
        jobRepository.deleteById(jobId);
        return "post deleted";
    }

    @Override
    public void updateJob(Job job) {
        if (job == null || job.getId() == null) {
            throw new CustomException("INVALID_JOB", "Job data or ID cannot be null");
        }
        if (!jobRepository.existsById(job.getId())) {
            throw new CustomException("JOB_NOT_FOUND", "Job with ID " + job.getId() + " not found");
        }
        jobRepository.save(job);
    }
}