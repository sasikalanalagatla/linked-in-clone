package com.org.linkedin.controller;

import com.org.linkedin.model.Job;
import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.JobRepository;
import com.org.linkedin.repository.SkillRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/job")
@Controller
public class JobController {

    private final SkillRepository skillRepository;
    private final JobService jobService;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobController(SkillRepository skillRepository, JobService jobService, JobRepository jobRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.jobService = jobService;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/job/feed")
    public String showJobFeed(Model model){
        List<Job> jobs = jobRepository.findAll();
        User user = userRepository.findById(1L).orElseThrow();
        model.addAttribute("jobs", jobs);
        model.addAttribute("user", user);
        return "/jobs-feed";
    }

    @PostMapping("/create")
    public String createPost(Job job){
        jobService.createJob(job);
        return "/add-job";
    }

    @GetMapping("/add")
    public String showForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("skills", skillRepository.findAll());
        return "/add-job";
    }

}
