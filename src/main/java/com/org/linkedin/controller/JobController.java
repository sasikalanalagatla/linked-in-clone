package com.org.linkedin.controller;

import com.org.linkedin.model.AdditionalQuestion;
import com.org.linkedin.model.Job;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.JobRepository;
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
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobController(SkillRepository skillRepository, JobServiceImpl jobServiceImpl, JobRepository jobRepository,
                         UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.jobServiceImpl = jobServiceImpl;
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

}
