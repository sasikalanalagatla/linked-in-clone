package com.org.linkedin.controller;

import com.org.linkedin.model.Experience;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ExperienceService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

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
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("experience", new Experience());
        return "add-experience";
    }

    @PostMapping("/add/experience/{userId}")
    public String addExperience(@PathVariable Long userId,
                                @ModelAttribute Experience experience,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            experienceService.addExperience(userId, experience);
            redirectAttributes.addFlashAttribute("success", "Experience added successfully!");
            return "redirect:/profile/" + userId;
        } catch (Exception e) {
            model.addAttribute("user", userService.getUserById(userId));
            model.addAttribute("experience", experience);
            model.addAttribute("error", "Error saving experience: " + e.getMessage());
            return "add-experience";
        }
    }

    @GetMapping("/edit/experience/{experienceId}")
    public String showEditForm(@PathVariable Long experienceId, Model model) {
        Experience experience = experienceService.getExperienceById(experienceId);
        if (experience == null) {
            model.addAttribute("error", "Experience not found");
            return "error";
        }
        model.addAttribute("experience", experience);
        model.addAttribute("user", experience.getUser());
        return "add-experience";
    }

    @PostMapping("/update/experience/{experienceId}")
    public String updateExperience(@PathVariable Long experienceId,
                                   @ModelAttribute Experience experience,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        try {
            experience.setExperienceId(experienceId);
            Experience updated = experienceService.updateExperience(experienceId, experience);
            redirectAttributes.addFlashAttribute("success", "Experience updated successfully!");
            return "redirect:/profile/" + updated.getUser().getUserId();
        } catch (Exception e) {
            Experience existing = experienceService.getExperienceById(experienceId);
            model.addAttribute("experience", experience);
            model.addAttribute("user", existing != null ? existing.getUser() : new User());
            model.addAttribute("error", "Error updating experience: " + e.getMessage());
            return "add-experience";
        }
    }

    @PostMapping("/delete/experience/{experienceId}")
    public String deleteExperience(@PathVariable Long experienceId, RedirectAttributes redirectAttributes) {
        try {
            Experience experience = experienceService.getExperienceById(experienceId);
            Long userId = experience.getUser().getUserId();
            experienceService.deleteExperience(experienceId);
            redirectAttributes.addFlashAttribute("success", "Experience deleted successfully!");
            return "redirect:/profile/" + userId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting experience: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    @GetMapping("/delete/experience/{experienceId}")
    public String deleteExperienceGet(@PathVariable Long experienceId, RedirectAttributes redirectAttributes) {
        return deleteExperience(experienceId, redirectAttributes);
    }
}