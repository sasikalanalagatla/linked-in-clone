package com.org.linkedin.controller;

import com.org.linkedin.model.AdditionalQuestion;
import com.org.linkedin.model.ApplyJob;
import com.org.linkedin.model.Job;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ApplyJobRepository;
import com.org.linkedin.repository.JobRepository;
import com.org.linkedin.repository.SkillRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.CloudinaryService;
import com.org.linkedin.service.impl.JobServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class JobController {

    private final SkillRepository skillRepository;
    private final JobServiceImpl jobServiceImpl;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplyJobRepository applyJobRepository;
    private final CloudinaryService cloudinaryService;

    public JobController(SkillRepository skillRepository, JobServiceImpl jobServiceImpl, JobRepository jobRepository,
                         UserRepository userRepository, ApplyJobRepository applyJobRepository, CloudinaryService cloudinaryService) {
        this.skillRepository = skillRepository;
        this.jobServiceImpl = jobServiceImpl;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.applyJobRepository = applyJobRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/job/feed")
    public String jobFeed(@RequestParam(value = "keyword", required = false) String keyword,
                          @RequestParam(value = "jobId", required = false) Long jobId,
                          @RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "size", defaultValue = "5") int size,
                          Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobPage;

        if (keyword != null && !keyword.isEmpty()) {
            jobPage = jobServiceImpl.searchJobs(keyword, pageable);
        } else {
            jobPage = jobServiceImpl.getAllJobs(pageable);
        }

        List<Job> jobs = jobPage.getContent();
        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("size", size);

        Job selectedJob = null;
        if (jobId != null) {
            selectedJob = jobServiceImpl.getJobById(jobId);
        } else if (!jobs.isEmpty()) {
            selectedJob = jobs.get(0);
        }

        model.addAttribute("selectedJob", selectedJob);

        return "jobs-feed";
    }

    @PostMapping("/job/create")
    public String createPost(@ModelAttribute("job") Job job,
                             @RequestParam(required = false) String addQuestion,
                             Model model) {

        if (addQuestion != null) {
            job.getAdditionalQuestions().add(new AdditionalQuestion());
            model.addAttribute("job", job);
            model.addAttribute("skills", skillRepository.findAll());
            return "add-job";
        }

        for (AdditionalQuestion question : job.getAdditionalQuestions()) {
            question.setJob(job);
        }

        jobServiceImpl.createJob(job);
        return "redirect:/job/feed";
    }

    @GetMapping("/job/add")
    public String showForm(Model model) {
        Job job = new Job();
        job.getAdditionalQuestions().add(new AdditionalQuestion());
        model.addAttribute("job", job);
        model.addAttribute("skills", skillRepository.findAll());
        return "add-job";
    }

    @GetMapping("/job/get/{jobId}")
    public String getJobById(@PathVariable("jobId") Long jobId, Model model){
        Job job = jobServiceImpl.getJobById(jobId);
        model.addAttribute("job",job);
        return "single-job";
    }

    @GetMapping("/")
    public String getHomePage(){
        return "home-page";
    }

    @GetMapping("/job/apply/{jobId}")
    public String showApplyForm(@PathVariable Long jobId, Model model) {
        Job job = jobServiceImpl.getJobById(jobId);
        ApplyJob applyJob = new ApplyJob();

        if (job.getAdditionalQuestions() != null) {
            for (int i = 0; i < job.getAdditionalQuestions().size(); i++) {
                applyJob.getAdditionalQuestionAnswers().add("");
            }
        }

        model.addAttribute("applyJob", applyJob);
        model.addAttribute("job", job);
        return "job-apply-form";
    }

    @PostMapping("/job/apply/{jobId}")
    public String submitApplyForm(@PathVariable Long jobId,
                                  @ModelAttribute ApplyJob applyJob,
                                  @RequestParam("resumeFile") MultipartFile resumeFile) {
        try {
            Job job = jobServiceImpl.getJobById(jobId);
            String resumeUrl = cloudinaryService.uploadFile(resumeFile);
            applyJob.setResumeUrl(resumeUrl);
            User user = userRepository.findByEmail(applyJob.getEmail());
            applyJob.setUser(user);

            applyJob.setJob(job);
            applyJobRepository.save(applyJob);

            return "redirect:/job/feed?jobId=" + jobId;

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/job/apply/" + jobId + "?error=true";
        }
    }


    @PostMapping("/job/delete/{jobId}")
    public String deleteJobById(@PathVariable("jobId") Long jobId){
        jobServiceImpl.deleteJobById(jobId);
        return "redirect:/job/feed";
    }

    @GetMapping("/job/edit/{id}")
    public String editJobForm(@PathVariable Long id, Model model) {
        Job job = jobServiceImpl.getJobById(id);
        model.addAttribute("job", job);
        return "edit-job";
    }

    @PostMapping("/job/update")
    public String updateJob(@ModelAttribute("job") Job updatedJob) {
        jobServiceImpl.updateJob(updatedJob);
        return "redirect:/job/feed";
    }
}