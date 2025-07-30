package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ApplyJob;
import com.org.linkedin.model.Job;
import com.org.linkedin.model.User;
import com.org.linkedin.service.JobService;
import com.org.linkedin.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;

@Controller
public class JobController {

    private final JobService jobService;
    private final UserService userService;

    public JobController(JobService jobService, UserService userService) {
        this.jobService = jobService;
        this.userService = userService;
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
            model.addAllAttributes(jobService.getJobFeedDetails(keyword, range, jobId, page, size, principal));
            return "jobs-feed";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/job/add")
    public String showForm(Model model) {
        try {
            model.addAttribute("job", new Job());
            model.addAttribute("skills", jobService.getAllSkills());
            model.addAttribute("companies", jobService.getAllCompanies());
            return "add-job";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/job/create")
    public String createPost(@Valid @ModelAttribute("job") Job job,
                             BindingResult result,
                             @RequestParam(required = false) String addQuestion,
                             Model model,
                             Principal principal) {
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to create a job");
            }
            if (result.hasErrors()) {
                model.addAttribute("error", "Validation failed: " + result.getAllErrors());
                model.addAttribute("skills", jobService.getAllSkills());
                model.addAttribute("companies", jobService.getAllCompanies());
                return "add-job";
            }
            if (addQuestion != null) {
                model.addAttribute("job", job);
                model.addAttribute("skills", jobService.getAllSkills());
                model.addAttribute("companies", jobService.getAllCompanies());
                return "add-job";
            }

            jobService.createJob(job, principal);
            return "redirect:/job/feed";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            model.addAttribute("job", job);
            model.addAttribute("skills", jobService.getAllSkills());
            model.addAttribute("companies", jobService.getAllCompanies());
            return "add-job";
        }
    }

    @GetMapping("/job/get/{jobId}")
    public String getJobById(@PathVariable("jobId") Long jobId,
                             Model model,
                             Principal principal) {
        try {
            Job job = jobService.getJobById(jobId);
            model.addAttribute("job", job);
            if (principal != null) {
                model.addAttribute("loggedInUser", jobService.getJobById(jobId).getUser());
            }
            return "single-job";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/job/apply/{jobId}")
    public String showApplyForm(@PathVariable Long jobId, Model model) {
        try {
            model.addAllAttributes(jobService.getJobApplyForm(jobId));
            return "job-apply-form";
        } catch (CustomException e) {
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
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("error", "Validation failed: " + result.getAllErrors());
                model.addAllAttributes(jobService.getJobApplyForm(jobId));
                return "job-apply-form";
            }
            Map<String, Object> resultMap = jobService.submitJobApplication(jobId, applyJob, resumeFile, principal);
            redirectAttributes.addFlashAttribute("success", "Job application submitted successfully!");
            return "redirect:" + resultMap.get("redirect");
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            model.addAllAttributes(jobService.getJobApplyForm(jobId));
            return "job-apply-form";
        }
    }

    @PostMapping("/job/delete/{jobId}")
    public String deleteJobById(@PathVariable("jobId") Long jobId, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to delete a job");
            }
            jobService.deleteJob(jobId, principal);
            redirectAttributes.addFlashAttribute("success", "Job deleted successfully!");
            return "redirect:/job/feed";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/job/edit/{id}")
    public String editJobForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("job", jobService.getJobById(id));
            model.addAttribute("skills", jobService.getAllSkills());
            model.addAttribute("companies", jobService.getAllCompanies());
            return "edit-job";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/job/update")
    public String updateJob(@Valid @ModelAttribute("job") Job updatedJob,
                            BindingResult result,
                            Principal principal,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to update a job");
            }
            if (result.hasErrors()) {
                model.addAttribute("error", "Validation failed: " + result.getAllErrors());
                model.addAttribute("skills", jobService.getAllSkills());
                model.addAttribute("companies", jobService.getAllCompanies());
                return "edit-job";
            }
            jobService.updateJob(updatedJob, principal);
            redirectAttributes.addFlashAttribute("success", "Job updated successfully!");
            return "redirect:/job/feed";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            model.addAttribute("job", updatedJob);
            model.addAttribute("skills", jobService.getAllSkills());
            model.addAttribute("companies", jobService.getAllCompanies());
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
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to view posted jobs");
            }

            User user = userService.findByEmail(principal.getName());
            model.addAllAttributes(jobService.getPostedJobsDetails(user.getUserId(), page, size, sortBy, sortDir));
            return "posted-jobs";
        } catch (CustomException e) {
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
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to view applied jobs");
            }
            User user = userService.findByEmail(principal.getName());
            model.addAllAttributes(jobService.getAppliedJobsDetails(user.getUserId(), page, size, sortBy, sortDir));
            return "applied-jobs";
        } catch (CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }
}