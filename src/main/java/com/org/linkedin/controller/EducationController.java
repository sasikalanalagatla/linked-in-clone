package com.org.linkedin.controller;

import com.org.linkedin.model.Education;
import com.org.linkedin.model.User;
import com.org.linkedin.service.impl.EducationServiceImpl;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("education", new Education());
        return "add-education";
    }

    @PostMapping("/add/education/{userId}")
    public String addEducation(@PathVariable("userId") Long userId,
                               @ModelAttribute Education education,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            educationService.addEducation(userId, education);
            redirectAttributes.addFlashAttribute("success", "Education added successfully!");
            return "redirect:/profile/" + userId;
        } catch (Exception e) {
            e.printStackTrace();
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
            Education education = educationService.getEducationById(educationId);
            User user = userService.getUserById(education.getUser().getUserId());

            model.addAttribute("education", education);
            model.addAttribute("user", user);
            return "add-education"; // Same template as add
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Education not found: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/update/education/{educationId}")
    public String updateEducation(@PathVariable("educationId") Long educationId,
                                  @ModelAttribute Education education,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            education.setEducationId(educationId);
            Education updatedEducation = educationService.updateEducation(educationId, education);

            redirectAttributes.addFlashAttribute("success", "Education updated successfully!");
            return "redirect:/profile/" + updatedEducation.getUser().getUserId();
        } catch (Exception e) {
            e.printStackTrace();

            try {
                Education existingEducation = educationService.getEducationById(educationId);
                User user = userService.getUserById(existingEducation.getUser().getUserId());
                model.addAttribute("user", user);
            } catch (Exception ex) {
                model.addAttribute("user", new User());
            }

            education.setEducationId(educationId);
            model.addAttribute("education", education);
            model.addAttribute("error", "Error updating education: " + e.getMessage());
            return "add-education";
        }
    }

    @PostMapping("/delete/education/{educationId}")
    public String deleteEducation(@PathVariable("educationId") Long educationId,
                                  RedirectAttributes redirectAttributes) {
        try {
            Education education = educationService.getEducationById(educationId);
            Long userId = education.getUser().getUserId();

            educationService.deleteEducation(educationId);
            redirectAttributes.addFlashAttribute("success", "Education deleted successfully!");
            return "redirect:/profile/" + userId;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error deleting education: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    @GetMapping("/delete/education/{educationId}")
    public String deleteEducationGet(@PathVariable("educationId") Long educationId,
                                     RedirectAttributes redirectAttributes) {
        return deleteEducation(educationId, redirectAttributes);
    }
}