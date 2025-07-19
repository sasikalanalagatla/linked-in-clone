package com.org.linkedin.controller;

import com.org.linkedin.model.Job;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.SkillRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.impl.JobServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class JobController {

    private final SkillRepository skillRepository;
    private final JobServiceImpl jobServiceImpl;
    private final UserRepository userRepository;

    public JobController(SkillRepository skillRepository, JobServiceImpl jobServiceImpl, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.jobServiceImpl = jobServiceImpl;
        this.userRepository = userRepository;
    }

    @GetMapping("/job/feed")
    public String showJobFeed(@RequestParam(required = false) Long jobId, Model model) {
        List<Job> jobs = jobServiceImpl.getAllJobs();
        Job selectedJob = (jobId != null) ? jobServiceImpl.getJobById(jobId) : (!jobs.isEmpty() ? jobs.get(0) : null);
        User user = userRepository.findById(1L).orElseThrow();

        model.addAttribute("jobs", jobs);
        model.addAttribute("selectedJob", selectedJob);
        model.addAttribute("user", user);

        return "jobs-feed";
    }

    @GetMapping("/job/add")
    public String showAddJobForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("skills", skillRepository.findAll());
        return "add-job"; // Form page
    }

    @PostMapping("/job/create")
    public String createJob(@ModelAttribute Job job) {
        Job savedJob = jobServiceImpl.createJob(job);
        return "redirect:/job/feed?jobId=" + savedJob.getId();
    }
}