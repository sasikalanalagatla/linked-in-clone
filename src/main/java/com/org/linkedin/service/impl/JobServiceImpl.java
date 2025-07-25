package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Job;
import com.org.linkedin.model.Company;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.CompanyRepository;
import com.org.linkedin.repository.JobRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public JobServiceImpl(JobRepository jobRepository, UserRepository userRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public Page<Job> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    @Override
    public Page<Job> searchJobs(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return jobRepository.findAll(pageable);
        }
        return jobRepository.searchJobsByTitleOrSkill(keyword.trim(), pageable);
    }

    @Override
    public Job getJobById(Long jobId) {
        return jobRepository.findByIdWithApplicants(jobId)
                .orElseThrow(() -> new CustomException("JOB_NOT_FOUND", "Job not found with id: " + jobId));
    }

    @Override
    public void createJob(Job job, Principal principal) {
        validateJob(job);
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with email: " + principal.getName()));

        Company company = companyRepository.findById(job.getCompany().getId())
                .orElseThrow(() -> new CustomException("COMPANY_NOT_FOUND", "Company not found with ID: " + job.getCompany().getId()));

        job.setUser(user);
        job.setCompany(company);
        job.setJobCreatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @Override
    public void updateJob(Job updatedJob, Principal principal) {
        validateJob(updatedJob);
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with email: " + principal.getName()));
        Job existingJob = jobRepository.findById(updatedJob.getId())
                .orElseThrow(() -> new CustomException("JOB_NOT_FOUND", "Job not found with ID: " + updatedJob.getId()));

        if (!existingJob.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException("UNAUTHORIZED", "User not authorized to update this job");
        }

        Company company = companyRepository.findById(updatedJob.getCompany().getId())
                .orElseThrow(() -> new CustomException("COMPANY_NOT_FOUND", "Company not found with ID: " + updatedJob.getCompany().getId()));

        existingJob.setJobTitle(updatedJob.getJobTitle());
        existingJob.setCompany(company);
        existingJob.setJobDescription(updatedJob.getJobDescription());
        existingJob.setJobLocation(updatedJob.getJobLocation());
        existingJob.setJobWorkPlaceTypes(updatedJob.getJobWorkPlaceTypes());
        existingJob.setJobTypes(updatedJob.getJobTypes());
        existingJob.setRecruiterEmail(updatedJob.getRecruiterEmail());
        existingJob.setExperienceLevel(updatedJob.getExperienceLevel());
        existingJob.setRequiredSkills(updatedJob.getRequiredSkills());
        existingJob.setAdditionalQuestions(updatedJob.getAdditionalQuestions());
        existingJob.setJobPostEdited(true);
        jobRepository.save(existingJob);
    }

    @Override
    public void deleteJob(Long jobId, Principal principal) {
        if (jobId == null) {
            throw new CustomException("JOB_ID_NULL", "Job ID cannot be null");
        }
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with email: " + principal.getName()));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException("JOB_NOT_FOUND", "Job not found with ID: " + jobId));

        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException("UNAUTHORIZED", "User not authorized to delete this job");
        }
        jobRepository.delete(job);
    }

    private void validateJob(Job job) {
        if (job == null) {
            throw new CustomException("JOB_NULL", "Job cannot be null");
        }
        if (job.getJobTitle() == null || job.getJobTitle().trim().isEmpty()) {
            throw new CustomException("INVALID_JOB_TITLE", "Job title cannot be empty");
        }
        if (job.getJobDescription() == null || job.getJobDescription().trim().isEmpty()) {
            throw new CustomException("INVALID_JOB_DESCRIPTION", "Job description cannot be empty");
        }
        if (job.getCompany() == null || job.getCompany().getId() == null) {
            throw new CustomException("INVALID_COMPANY", "Company ID cannot be null");
        }
    }
}