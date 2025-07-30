package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Education;
import com.org.linkedin.model.User;
import com.org.linkedin.service.UserService;
import com.org.linkedin.service.impl.EducationServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EducationController {

    private final EducationServiceImpl educationService;
    private final UserService userService;

    public EducationController(EducationServiceImpl educationService, UserService userService) {
        this.educationService = educationService;
        this.userService = userService;
    }

    @GetMapping("/new/education/{userId}")
    public String showEducationForm(@PathVariable("userId") Long userId, Model model) {
        try {
            if (userId == null) {
                throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
            }
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("education", new Education());
            return "add-education";
        } catch (CustomException e) {
            model.addAttribute("error", "Error loading education form: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/add/education/{userId}")
    public String addEducation(@PathVariable("userId") Long userId,
                               @ModelAttribute Education education,
                               Model model) {
        try {
            if (userId == null) {
                throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
            }
            if (education == null) {
                throw new CustomException("INVALID_EDUCATION", "Education data cannot be null");
            }
            educationService.addEducation(userId, education);
            return "redirect:/profile/" + userId;
        } catch (CustomException e) {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("education", education);
            model.addAttribute("error", "Error saving education: " + e.getMessage());
            return "add-education";
        }
    }

    @GetMapping("/edit/education/{educationId}")
    public String showEditEducationForm(@PathVariable("educationId") Long educationId, Model model) {
        try {
            if (educationId == null) {
                throw new CustomException("INVALID_EDUCATION_ID", "Education ID cannot be null");
            }
            Education education = educationService.getEducationById(educationId);
            User user = userService.getUserById(education.getUser().getUserId());
            model.addAttribute("education", education);
            model.addAttribute("user", user);
            return "add-education";
        } catch (CustomException e) {
            model.addAttribute("error", "Error loading education form: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/update/education/{educationId}")
    public String updateEducation(@PathVariable("educationId") Long educationId,
                                  @ModelAttribute Education education,
                                  Model model) {
        try {
            if (educationId == null) {
                throw new CustomException("INVALID_EDUCATION_ID", "Education ID cannot be null");
            }
            if (education == null) {
                throw new CustomException("INVALID_EDUCATION", "Education data cannot be null");
            }
            Education updatedEducation = educationService.updateEducation(educationId, education);
            return "redirect:/profile/" + updatedEducation.getUser().getUserId();
        } catch (CustomException e) {
            User user = userService.getUserById(education.getUser().getUserId());
            model.addAttribute("user", user);
            model.addAttribute("education", education);
            model.addAttribute("error", "Error updating education: " + e.getMessage());
            return "add-education";
        }
    }

    @PostMapping("/delete/education/{educationId}")
    public String deleteEducation(@PathVariable("educationId") Long educationId, Model model) {
        try {
            if (educationId == null) {
                throw new CustomException("INVALID_EDUCATION_ID", "Education ID cannot be null");
            }
            Education education = educationService.getEducationById(educationId);
            Long userId = education.getUser().getUserId();
            educationService.deleteEducation(educationId);
            return "redirect:/profile/" + userId;
        } catch (CustomException e) {
            model.addAttribute("error", "Error deleting education: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/delete/education/{educationId}")
    public String deleteEducationGet(@PathVariable("educationId") Long educationId, Model model) {
        try {
            if (educationId == null) {
                throw new CustomException("INVALID_EDUCATION_ID", "Education ID cannot be null");
            }
            Education education = educationService.getEducationById(educationId);
            Long userId = education.getUser().getUserId();
            educationService.deleteEducation(educationId);
            return "redirect:/profile/" + userId;
        } catch (CustomException e) {
            model.addAttribute("error", "Error deleting education: " + e.getMessage());
            return "error";
        }
    }
}