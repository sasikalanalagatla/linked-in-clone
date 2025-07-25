package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.*;
import com.org.linkedin.repository.*;
import com.org.linkedin.service.impl.CloudinaryService;
import com.org.linkedin.service.impl.JobServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class JobController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    private final SkillRepository skillRepository;
    private final JobServiceImpl jobServiceImpl;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplyJobRepository applyJobRepository;
    private final CloudinaryService cloudinaryService;
    private final CompanyRepository companyRepository;

    public JobController(SkillRepository skillRepository,
                         JobServiceImpl jobServiceImpl,
                         JobRepository jobRepository,
                         UserRepository userRepository,
                         ApplyJobRepository applyJobRepository,
                         CloudinaryService cloudinaryService,
                         CompanyRepository companyRepository) {
        this.skillRepository = skillRepository;
        this.jobServiceImpl = jobServiceImpl;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.applyJobRepository = applyJobRepository;
        this.cloudinaryService = cloudinaryService;
        this.companyRepository = companyRepository;
    }

    @GetMapping("/job/feed")
    public String jobFeed(@RequestParam(value = "keyword", required = false) String keyword,
                          @RequestParam(value = "range", required = false) String range,
                          @RequestParam(value = "jobId", required = false) Long jobId,
                          @RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "size", defaultValue = "5") int size,
                          Model model,
                          Principal principal) {
        try {
            logger.info("Accessing /job/feed with keyword: {}, range: {}, jobId: {}, page: {}, size: {}",
                    keyword, range, jobId, page, size);
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
                jobPage = jobRepository.filterAndSearch(keyword, createdAfter, pageable);
            } else if (keyword != null && !keyword.isEmpty()) {
                jobPage = jobServiceImpl.searchJobs(keyword, pageable);
            } else if (createdAfter != null) {
                jobPage = jobRepository.filterByCreatedAt(createdAfter, pageable);
            } else {
                jobPage = jobServiceImpl.getAllJobs(pageable);
            }

            List<Job> jobs = jobPage.getContent();
            model.addAttribute("jobs", jobs);
            model.addAttribute("keyword", keyword);
            model.addAttribute("range", range);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", jobPage.getTotalPages());
            model.addAttribute("size", size);

            Job selectedJob = (jobId != null)
                    ? jobServiceImpl.getJobById(jobId)
                    : jobs.isEmpty() ? null : jobs.get(0);

            model.addAttribute("selectedJob", selectedJob);

            if (principal != null) {
                User user = userRepository.findByEmail(principal.getName())
                        .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with email: " + principal.getName()));
                model.addAttribute("loggedInUser", user);
                Set<Long> appliedJobIds = applyJobRepository.findAppliedJobIdsByUserUserId(user.getUserId());
                model.addAttribute("appliedJobIds", appliedJobIds);
            }

            return "jobs-feed";
        } catch (CustomException e) {
            logger.error("Error in /job/feed: {}", e.getMessage());
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/job/add")
    public String showForm(Model model) {
        logger.info("Accessing /job/add");
        try {
            Job job = new Job();
            job.setAdditionalQuestions(new ArrayList<>());
            job.getAdditionalQuestions().add(new AdditionalQuestion());
            model.addAttribute("job", job);
            List<Skill> skills = skillRepository.findAll();
            List<Company> companies = companyRepository.findAll();
            model.addAttribute("skills", skills != null ? skills : new ArrayList<>());
            model.addAttribute("companies", companies != null ? companies : new ArrayList<>());
            return "add-job";
        } catch (Exception e) {
            logger.error("Error in /job/add: {}", e.getMessage());
            model.addAttribute("error", "SYSTEM_ERROR: Failed to load job form");
            return "error";
        }
    }

    @PostMapping("/job/create")
    public String createPost(@Valid @ModelAttribute("job") Job job,
                             BindingResult result,
                             @RequestParam(required = false) String addQuestion,
                             Model model,
                             Principal principal) {
        logger.info("Processing /job/create, addQuestion: {}", addQuestion);
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to create a job");
            }
            if (result.hasErrors()) {
                logger.warn("Validation errors: {}", result.getAllErrors());
                model.addAttribute("error", "Validation failed: " + result.getAllErrors());
                model.addAttribute("skills", skillRepository.findAll());
                model.addAttribute("companies", companyRepository.findAll());
                return "add-job";
            }
            if (addQuestion != null) {
                job.getAdditionalQuestions().add(new AdditionalQuestion());
                model.addAttribute("job", job);
                model.addAttribute("skills", skillRepository.findAll());
                model.addAttribute("companies", companyRepository.findAll());
                return "add-job";
            }

            // Safely remove empty questions
            job.getAdditionalQuestions().removeIf(
                    question -> question.getQuestion() == null || question.getQuestion().trim().isEmpty()
            );
            for (AdditionalQuestion question : job.getAdditionalQuestions()) {
                question.setJob(job);
            }

            jobServiceImpl.createJob(job, principal);
            return "redirect:/job/feed";
        } catch (CustomException e) {
            logger.error("Error in /job/create: {}", e.getMessage());
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            model.addAttribute("job", job);
            model.addAttribute("skills", skillRepository.findAll());
            model.addAttribute("companies", companyRepository.findAll());
            return "add-job";
        }
    }

    @GetMapping("/job/get/{jobId}")
    public String getJobById(@PathVariable("jobId") Long jobId,
                             Model model,
                             Principal principal) {
        try {
            logger.info("Accessing /job/get/{}", jobId);
            Job job = jobServiceImpl.getJobById(jobId);
            model.addAttribute("job", job);

            if (principal != null) {
                User user = userRepository.findByEmail(principal.getName())
                        .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with email: " + principal.getName()));
                model.addAttribute("loggedInUser", user);
            }

            return "single-job";
        } catch (CustomException e) {
            logger.error("Error in /job/get/{}: {}", jobId, e.getMessage());
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/job/apply/{jobId}")
    public String showApplyForm(@PathVariable Long jobId, Model model) {
        try {
            logger.info("Accessing /job/apply/{}", jobId);
            Job job = jobServiceImpl.getJobById(jobId);
            ApplyJob applyJob = new ApplyJob();
            applyJob.setAdditionalQuestionAnswers(new ArrayList<>());

            if (job.getAdditionalQuestions() != null) {
                for (int i = 0; i < job.getAdditionalQuestions().size(); i++) {
                    applyJob.getAdditionalQuestionAnswers().add("");
                }
            }

            model.addAttribute("applyJob", applyJob);
            model.addAttribute("job", job);
            return "job-apply-form";
        } catch (CustomException e) {
            logger.error("Error in /job/apply/{}: {}", jobId, e.getMessage());
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/job/apply/{jobId}")
    public String submitApplyForm(@PathVariable Long jobId,
                                  @Valid @ModelAttribute ApplyJob applyJob,
                                  BindingResult result,
                                  @RequestParam("resumeFile") MultipartFile resumeFile,
                                  Principal principal,
                                  Model model) {
        try {
            logger.info("Processing /job/apply/{}", jobId);
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to apply for a job");
            }
            Job job = jobServiceImpl.getJobById(jobId);
            if (result.hasErrors()) {
                logger.warn("Validation errors: {}", result.getAllErrors());
                model.addAttribute("error", "Validation failed: " + result.getAllErrors());
                model.addAttribute("applyJob", applyJob);
                model.addAttribute("job", job);
                return "job-apply-form";
            }
            if (resumeFile == null || resumeFile.isEmpty()) {
                throw new CustomException("INVALID_RESUME", "Resume file is required");
            }
            if (!resumeFile.getContentType().equals("application/pdf")) {
                throw new CustomException("INVALID_RESUME", "Resume must be a PDF file");
            }
            String resumeUrl = cloudinaryService.uploadFile(resumeFile);
            applyJob.setResumeUrl(resumeUrl);

            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with email: " + principal.getName()));
            applyJob.setUser(user);
            applyJob.setJob(job);

            applyJobRepository.save(applyJob);
            return "redirect:/job/feed?jobId=" + jobId;
        } catch (CustomException e) {
            logger.error("Error in /job/apply/{}: {}", jobId, e.getMessage());
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            model.addAttribute("applyJob", applyJob);
            model.addAttribute("job", jobServiceImpl.getJobById(jobId));
            return "job-apply-form";
        } catch (IOException e) {
            logger.error("IO Error in /job/apply/{}: {}", jobId, e.getMessage());
            model.addAttribute("error", "SYSTEM_ERROR: Failed to upload resume");
            model.addAttribute("applyJob", applyJob);
            model.addAttribute("job", jobServiceImpl.getJobById(jobId));
            return "job-apply-form";
        }
    }

    @PostMapping("/job/delete/{jobId}")
    public String deleteJobById(@PathVariable("jobId") Long jobId, Principal principal, Model model) {
        try {
            logger.info("Processing /job/delete/{}", jobId);
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to delete a job");
            }
            jobServiceImpl.deleteJob(jobId, principal);
            return "redirect:/job/feed";
        } catch (CustomException e) {
            logger.error("Error in /job/delete/{}: {}", jobId, e.getMessage());
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/job/edit/{id}")
    public String editJobForm(@PathVariable Long id, Model model) {
        try {
            logger.info("Accessing /job/edit/{}", id);
            Job job = jobServiceImpl.getJobById(id);
            model.addAttribute("job", job);
            model.addAttribute("skills", skillRepository.findAll());
            model.addAttribute("companies", companyRepository.findAll());
            return "edit-job";
        } catch (CustomException e) {
            logger.error("Error in /job/edit/{}: {}", id, e.getMessage());
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/job/update")
    public String updateJob(@Valid @ModelAttribute("job") Job updatedJob,
                            BindingResult result,
                            Principal principal,
                            Model model) {
        try {
            logger.info("Processing /job/update for job ID: {}", updatedJob.getId());
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to update a job");
            }
            if (result.hasErrors()) {
                logger.warn("Validation errors: {}", result.getAllErrors());
                model.addAttribute("error", "Validation failed: " + result.getAllErrors());
                model.addAttribute("skills", skillRepository.findAll());
                model.addAttribute("companies", companyRepository.findAll());
                return "edit-job";
            }

            // Get the existing job from database to preserve the collection
            Job existingJob = jobServiceImpl.getJobById(updatedJob.getId());

            // Update the basic fields
            existingJob.setJobTitle(updatedJob.getJobTitle());
            existingJob.setCompany(updatedJob.getCompany());
            existingJob.setJobLocation(updatedJob.getJobLocation());
            existingJob.setJobDescription(updatedJob.getJobDescription());
            existingJob.setJobTypes(updatedJob.getJobTypes());
            existingJob.setJobWorkPlaceTypes(updatedJob.getJobWorkPlaceTypes());
            existingJob.setRecruiterEmail(updatedJob.getRecruiterEmail());

            // Handle additional questions if they exist
            if (updatedJob.getAdditionalQuestions() != null) {
                // Clear existing questions (this will trigger orphan removal properly)
                existingJob.getAdditionalQuestions().clear();

                // Add new questions
                for (AdditionalQuestion question : updatedJob.getAdditionalQuestions()) {
                    if (question.getQuestion() != null && !question.getQuestion().trim().isEmpty()) {
                        question.setJob(existingJob);
                        existingJob.getAdditionalQuestions().add(question);
                    }
                }
            }

            jobServiceImpl.updateJob(existingJob, principal);
            return "redirect:/job/feed";
        } catch (CustomException e) {
            logger.error("Error in /job/update: {}", e.getMessage());
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            model.addAttribute("job", updatedJob);
            model.addAttribute("skills", skillRepository.findAll());
            model.addAttribute("companies", companyRepository.findAll());
            return "edit-job";
        }
    }

    @GetMapping("/job/posted")
    public String getPostedJobs(@RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "10") int size,
                                @RequestParam(value = "sortBy", defaultValue = "jobCreatedAt") String sortBy,
                                @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                                Model model,
                                Principal principal) {
        try {
            logger.info("Accessing /job/posted with page: {}, size: {}, sortBy: {}, sortDir: {}",
                    page, size, sortBy, sortDir);
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to view posted jobs");
            }
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with email: " + principal.getName()));

            String validatedSortBy = sortBy;
            if (!Arrays.asList("jobCreatedAt", "jobTitle", "company").contains(sortBy)) {
                validatedSortBy = "jobCreatedAt";
            }

            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(validatedSortBy).descending() :
                    Sort.by(validatedSortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Job> postedJobsPage = jobRepository.findByUserUserId(user.getUserId(), pageable);

            List<Job> jobsWithCounts = postedJobsPage.getContent().stream()
                    .map(job -> {
                        Long applicationsCount = applyJobRepository.countByJobId(job.getId());
                        job.setApplicationsCount(applicationsCount);
                        return job;
                    })
                    .toList();

            model.addAttribute("postedJobs", jobsWithCounts);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", postedJobsPage.getTotalPages());
            model.addAttribute("totalElements", postedJobsPage.getTotalElements());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("size", size);
            model.addAttribute("loggedInUser", user);

            return "posted-jobs";
        } catch (CustomException e) {
            logger.error("Error in /job/posted: {}", e.getMessage());
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/job/applied")
    public String getAppliedJobs(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 @RequestParam(value = "sortBy", defaultValue = "appliedAt") String sortBy,
                                 @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                                 Model model,
                                 Principal principal) {
        try {
            logger.info("Accessing /job/applied with page: {}, size: {}, sortBy: {}, sortDir: {}",
                    page, size, sortBy, sortDir);
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to view applied jobs");
            }
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found with email: " + principal.getName()));

            String validatedSortBy = sortBy;
            if (!Arrays.asList("appliedAt", "job.jobTitle", "job.company").contains(sortBy)) {
                validatedSortBy = "appliedAt";
            }

            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(validatedSortBy).descending() :
                    Sort.by(validatedSortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ApplyJob> appliedJobsPage = applyJobRepository.findByUserUserId(user.getUserId(), pageable);

            model.addAttribute("appliedJobs", appliedJobsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", appliedJobsPage.getTotalPages());
            model.addAttribute("totalElements", appliedJobsPage.getTotalElements());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("size", size);
            model.addAttribute("loggedInUser", user);

            return "applied-jobs";
        } catch (CustomException e) {
            logger.error("Error in /job/applied: {}", e.getMessage());
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }
}