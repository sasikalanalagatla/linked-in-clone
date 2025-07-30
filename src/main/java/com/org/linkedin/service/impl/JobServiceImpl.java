package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ApplyJob;
import com.org.linkedin.model.Company;
import com.org.linkedin.model.Job;
import com.org.linkedin.model.Skill;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ApplyJobRepository;
import com.org.linkedin.repository.CompanyRepository;
import com.org.linkedin.repository.JobRepository;
import com.org.linkedin.repository.SkillRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ApplyJobRepository applyJobRepository;
    private final SkillRepository skillRepository;
    private final CloudinaryService cloudinaryService;

    public JobServiceImpl(JobRepository jobRepository, UserRepository userRepository,
                          CompanyRepository companyRepository, ApplyJobRepository applyJobRepository,
                          SkillRepository skillRepository, CloudinaryService cloudinaryService) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.applyJobRepository = applyJobRepository;
        this.skillRepository = skillRepository;
        this.cloudinaryService = cloudinaryService;
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

    @Override
    public Page<Job> filterAndSearchJobs(String keyword, LocalDateTime createdAfter, Pageable pageable) {
        return jobRepository.filterAndSearch(keyword, createdAfter, pageable);
    }

    @Override
    public Page<Job> filterByCreatedAt(LocalDateTime createdAfter, Pageable pageable) {
        return jobRepository.filterByCreatedAt(createdAfter, pageable);
    }

    @Override
    public Set<Long> getAppliedJobIdsByUserId(Long userId) {
        return applyJobRepository.findAppliedJobIdsByUserUserId(userId);
    }

    @Override
    public Page<ApplyJob> getAppliedJobsByUserId(Long userId, Pageable pageable) {
        return applyJobRepository.findByUserUserId(userId, pageable);
    }

    @Override
    public Page<Job> getPostedJobsByUserId(Long userId, Pageable pageable) {
        return jobRepository.findByUserUserId(userId, pageable);
    }

    @Override
    public Long countApplicationsByJobId(Long jobId) {
        return applyJobRepository.countByJobId(jobId);
    }

    @Override
    public void applyForJob(ApplyJob applyJob) {
        if (applyJob == null) {
            throw new CustomException("INVALID_APPLICATION", "Job application cannot be null");
        }
        applyJobRepository.save(applyJob);
    }

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Map<String, Object> getJobFeedDetails(String keyword, String range, Long jobId, int page, int size, Principal principal) {
        Map<String, Object> modelAttributes = new HashMap<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by("jobCreatedAt").descending());
        Page<Job> jobPage;

        LocalDateTime createdAfter = null;
        if ("past 24 hours".equalsIgnoreCase(range)) {
            createdAfter = LocalDateTime.now().minusHours(24);
        } else if ("past 7 days".equalsIgnoreCase(range)) {
            createdAfter = LocalDateTime.now().minusDays(7);
        } else if ("past 30 days".equalsIgnoreCase(range)) {
            createdAfter = LocalDateTime.now().minusDays(30);
        }

        if (keyword != null && !keyword.isEmpty() && createdAfter != null) {
            jobPage = filterAndSearchJobs(keyword, createdAfter, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            jobPage = searchJobs(keyword, pageable);
        } else if (createdAfter != null) {
            jobPage = filterByCreatedAt(createdAfter, pageable);
        } else {
            jobPage = getAllJobs(pageable);
        }

        List<Job> jobs = jobPage.getContent();
        modelAttributes.put("jobs", jobs);
        modelAttributes.put("keyword", keyword);
        modelAttributes.put("range", range);
        modelAttributes.put("currentPage", page);
        modelAttributes.put("totalPages", jobPage.getTotalPages());
        modelAttributes.put("size", size);

        Job selectedJob = (jobId != null)
                ? getJobById(jobId)
                : jobs.isEmpty() ? null : jobs.get(0);
        modelAttributes.put("selectedJob", selectedJob);

        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with email: " + principal.getName()));
            modelAttributes.put("loggedInUser", user);
            Set<Long> appliedJobIds = getAppliedJobIdsByUserId(user.getUserId());
            modelAttributes.put("appliedJobIds", appliedJobIds);
        }

        return modelAttributes;
    }

    @Override
    public Map<String, Object> getJobApplyForm(Long jobId) {
        Map<String, Object> modelAttributes = new HashMap<>();
        Job job = getJobById(jobId);
        ApplyJob applyJob = new ApplyJob();
        modelAttributes.put("applyJob", applyJob);
        modelAttributes.put("job", job);
        return modelAttributes;
    }

    @Override
    public Map<String, Object> submitJobApplication(Long jobId, ApplyJob applyJob, MultipartFile resumeFile, Principal principal) {
        Map<String, Object> modelAttributes = new HashMap<>();
        if (principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in to apply for a job");
        }
        Job job = getJobById(jobId);
        if (resumeFile == null || resumeFile.isEmpty()) {
            throw new CustomException("INVALID_RESUME", "Resume file is required");
        }
        if (!resumeFile.getContentType().equals("application/pdf")) {
            throw new CustomException("INVALID_RESUME", "Resume must be a PDF file");
        }
        String resumeUrl;
        try {
            resumeUrl = cloudinaryService.uploadFile(resumeFile);
        } catch (IOException e) {
            throw new CustomException("SYSTEM_ERROR", "Failed to upload resume");
        }
        applyJob.setResumeUrl(resumeUrl);

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with email: " + principal.getName()));
        applyJob.setUser(user);
        applyJob.setJob(job);

        applyForJob(applyJob);
        modelAttributes.put("redirect", "/job/feed?jobId=" + jobId);
        return modelAttributes;
    }

    @Override
    public Map<String, Object> getPostedJobsDetails(Long userId, int page, int size, String sortBy, String sortDir) {
        Map<String, Object> modelAttributes = new HashMap<>();
        String validatedSortBy = sortBy;
        if (!Arrays.asList("jobCreatedAt", "jobTitle", "company").contains(sortBy)) {
            validatedSortBy = "jobCreatedAt";
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(validatedSortBy).descending() :
                Sort.by(validatedSortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Job> postedJobsPage = getPostedJobsByUserId(userId, pageable);

        List<Job> jobsWithCounts = postedJobsPage.getContent().stream()
                .map(job -> {
                    Long applicationsCount = countApplicationsByJobId(job.getId());
                    job.setApplicationsCount(applicationsCount);
                    return job;
                })
                .toList();

        modelAttributes.put("postedJobs", jobsWithCounts);
        modelAttributes.put("currentPage", page);
        modelAttributes.put("totalPages", postedJobsPage.getTotalPages());
        modelAttributes.put("totalElements", postedJobsPage.getTotalElements());
        modelAttributes.put("sortBy", sortBy);
        modelAttributes.put("sortDir", sortDir);
        modelAttributes.put("size", size);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with ID: " + userId));
        modelAttributes.put("loggedInUser", user);

        return modelAttributes;
    }

    @Override
    public Map<String, Object> getAppliedJobsDetails(Long userId, int page, int size, String sortBy, String sortDir) {
        Map<String, Object> modelAttributes = new HashMap<>();
        String validatedSortBy = sortBy;
        if (!Arrays.asList("appliedAt", "job.jobTitle", "job.company").contains(sortBy)) {
            validatedSortBy = "appliedAt";
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(validatedSortBy).descending() :
                Sort.by(validatedSortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ApplyJob> appliedJobsPage = getAppliedJobsByUserId(userId, pageable);

        modelAttributes.put("appliedJobs", appliedJobsPage.getContent());
        modelAttributes.put("currentPage", page);
        modelAttributes.put("totalPages", appliedJobsPage.getTotalPages());
        modelAttributes.put("totalElements", appliedJobsPage.getTotalElements());
        modelAttributes.put("sortBy", sortBy);
        modelAttributes.put("sortDir", sortDir);
        modelAttributes.put("size", size);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with ID: " + userId));
        modelAttributes.put("loggedInUser", user);

        return modelAttributes;
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