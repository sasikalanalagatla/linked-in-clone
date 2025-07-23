package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Experience;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ExperienceService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ExperienceController {

    private final ExperienceService experienceService;
    private final UserService userService;

    public ExperienceController(ExperienceService experienceService, UserService userService) {
        this.experienceService = experienceService;
        this.userService = userService;
    }

    @GetMapping("/new/experience/{userId}")
    public String showExperienceForm(@PathVariable Long userId, Model model) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("experience", new Experience());
        return "add-experience";
    }

    @PostMapping("/add/experience/{userId}")
    public String addExperience(@PathVariable Long userId, @ModelAttribute Experience experience, Model model) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        if (experience == null) {
            throw new CustomException("INVALID_EXPERIENCE", "Experience data cannot be null");
        }
        try {
            experienceService.addExperience(userId, experience);
            return "redirect:/profile/" + userId;
        } catch (CustomException e) {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("experience", experience);
            model.addAttribute("error", "Error saving experience: " + e.getMessage());
            return "add-experience";
        }
    }

    @GetMapping("/edit/experience/{experienceId}")
    public String showEditForm(@PathVariable Long experienceId, Model model) {
        if (experienceId == null) {
            throw new CustomException("INVALID_EXPERIENCE_ID", "Experience ID cannot be null");
        }
        Experience experience = experienceService.getExperienceById(experienceId);
        model.addAttribute("experience", experience);
        model.addAttribute("user", experience.getUser());
        return "add-experience";
    }

    @PostMapping("/update/experience/{experienceId}")
    public String updateExperience(@PathVariable Long experienceId, @ModelAttribute Experience experience, Model model) {
        if (experienceId == null) {
            throw new CustomException("INVALID_EXPERIENCE_ID", "Experience ID cannot be null");
        }
        if (experience == null) {
            throw new CustomException("INVALID_EXPERIENCE", "Experience data cannot be null");
        }
        try {
            Experience updated = experienceService.updateExperience(experienceId, experience);
            return "redirect:/profile/" + updated.getUser().getUserId();
        } catch (CustomException e) {
            User user = experienceService.getExperienceById(experienceId).getUser();
            model.addAttribute("experience", experience);
            model.addAttribute("user", user);
            model.addAttribute("error", "Error updating experience: " + e.getMessage());
            return "add-experience";
        }
    }

    @PostMapping("/delete/experience/{experienceId}")
    public String deleteExperience(@PathVariable Long experienceId, Model model) {
        if (experienceId == null) {
            throw new CustomException("INVALID_EXPERIENCE_ID", "Experience ID cannot be null");
        }
        try {
            Experience experience = experienceService.getExperienceById(experienceId);
            Long userId = experience.getUser().getUserId();
            experienceService.deleteExperience(experienceId);
            return "redirect:/profile/" + userId;
        } catch (CustomException e) {
            model.addAttribute("error", "Error deleting experience: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/delete/experience/{experienceId}")
    public String deleteExperienceGet(@PathVariable Long experienceId, Model model) {
        return deleteExperience(experienceId, model);
    }
}