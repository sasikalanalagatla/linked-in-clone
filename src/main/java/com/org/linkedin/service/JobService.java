package com.org.linkedin.service;

import com.org.linkedin.model.ApplyJob;
import com.org.linkedin.model.Company;
import com.org.linkedin.model.Job;
import com.org.linkedin.model.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface JobService {

    Page<Job> getAllJobs(Pageable pageable);
    Page<Job> searchJobs(String keyword, Pageable pageable);
    Job getJobById(Long jobId);
    void createJob(Job job, Principal principal);
    void updateJob(Job job, Principal principal);
    void deleteJob(Long jobId, Principal principal);
    Page<Job> filterAndSearchJobs(String keyword, LocalDateTime createdAfter, Pageable pageable);
    Page<Job> filterByCreatedAt(LocalDateTime createdAfter, Pageable pageable);
    Set<Long> getAppliedJobIdsByUserId(Long userId);
    Page<ApplyJob> getAppliedJobsByUserId(Long userId, Pageable pageable);
    Page<Job> getPostedJobsByUserId(Long userId, Pageable pageable);
    Long countApplicationsByJobId(Long jobId);
    void applyForJob(ApplyJob applyJob);
    List<Skill> getAllSkills();
    List<Company> getAllCompanies();
}