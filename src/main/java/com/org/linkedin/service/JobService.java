package com.org.linkedin.service;

import com.org.linkedin.model.ApplyJob;
import com.org.linkedin.model.Company;
import com.org.linkedin.model.Job;
import com.org.linkedin.model.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface JobService {

    Page<Job> getAllJobs(Pageable pageable);
    Page<Job> searchJobs(String keyword, Pageable pageable);
    Job getJobById(Long jobId);
    void createJob(Job job, Principal principal);
    void updateJob(Job updatedJob, Principal principal);
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
    Map<String, Object> getJobFeedDetails(String keyword, String range, Long jobId, int page, int size, Principal principal);
    Map<String, Object> getJobApplyForm(Long jobId);
    Map<String, Object> submitJobApplication(Long jobId, ApplyJob applyJob, MultipartFile resumeFile, Principal principal);
    Map<String, Object> getPostedJobsDetails(Long userId, int page, int size, String sortBy, String sortDir);
    Map<String, Object> getAppliedJobsDetails(Long userId, int page, int size, String sortBy, String sortDir);
}