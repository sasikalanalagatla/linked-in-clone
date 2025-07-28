package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Company;
import com.org.linkedin.model.Job;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ApplyJobRepository;
import com.org.linkedin.repository.JobRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.UserService;
import com.org.linkedin.service.impl.CompanyServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
public class CompanyController {

    private final CompanyServiceImpl companyService;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplyJobRepository applyJobRepository;
    private final UserService userService;

    public CompanyController(CompanyServiceImpl companyService, JobRepository jobRepository, UserRepository userRepository, ApplyJobRepository applyJobRepository, UserService userService) {
        this.companyService = companyService;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.applyJobRepository = applyJobRepository;
        this.userService = userService;
    }

    @GetMapping("/company/add")
    public String showCompanyForm(Model model, Principal principal) {
        try {
            if(principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to create a company");
            }
            model.addAttribute("company", new Company());
            return "company-form"; // Use unified template
        } catch(CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/company/create")
    public String createCompany(@ModelAttribute("company") Company company, Principal principal, Model model) {
        try {
            companyService.createCompany(company, principal);
            return "redirect:/company/" + company.getId();
        } catch(CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            model.addAttribute("company", company);
            return "company-form"; // Use unified template
        }
    }

    @GetMapping("/company/{id}")
    public String getCompanyById(@PathVariable("id") Long id, Model model, Principal principal) {
        try {
            Company company = companyService.getCompanyById(id);
            List<Job> jobs = jobRepository.findByCompanyId(id);
            jobs.forEach(job -> {
                Long applicationsCount = applyJobRepository.countByJobId(job.getId());
                job.setApplicationsCount(applicationsCount);
            });
            model.addAttribute("company", company);
            model.addAttribute("jobs", jobs);
            if(principal != null) {
                userRepository.findByEmail(principal.getName()).ifPresent(user -> {
                    boolean isFollowing = companyService.isUserFollowing(user, company);
                    model.addAttribute("loggedInUser", user);
                    model.addAttribute("isFollowing", isFollowing);
                    Set<Long> appliedJobIds = applyJobRepository.findAppliedJobIdsByUserUserId(user.getUserId());
                    model.addAttribute("appliedJobIds", appliedJobIds);
                });
            }
            return "company";
        } catch(CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/company/follow/{id}")
    public String followCompany(@PathVariable Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        companyService.followCompany(user, id);
        return "redirect:/company/" + id;
    }

    @PostMapping("/company/unfollow/{id}")
    public String unfollowCompany(@PathVariable Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        companyService.unfollowCompany(user, id);
        return "redirect:/mynetwork";
    }

    @GetMapping("/company/edit/{id}")
    public String showEditCompanyForm(@PathVariable("id") Long id, Model model, Principal principal) {
        try {
            if(principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to edit a company");
            }
            Company company = companyService.getCompanyById(id);
            model.addAttribute("company", company);
            return "company-form"; // Use unified template
        } catch(CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/company/update/{id}")
    public String updateCompany(@PathVariable("id") Long id, @ModelAttribute("company") Company company, Principal principal, Model model) {
        try {
            company.setId(id);
            companyService.updateCompany(company, principal);
            return "redirect:/company/" + id;
        } catch(CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            model.addAttribute("company", company);
            return "company-form"; // Use unified template
        }
    }

    @PostMapping("/company/delete/{id}")
    public String deleteCompany(@PathVariable("id") Long id, Principal principal, Model model) {
        try {
            if(principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in to delete a company");
            }
            companyService.deleteCompany(id, principal);
            return "redirect:/";
        } catch(CustomException e) {
            model.addAttribute("error", e.getErrorCode() + ": " + e.getMessage());
            return "error";
        }
    }
}