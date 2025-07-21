package com.org.linkedin.controller;

import com.org.linkedin.model.Experience;
import com.org.linkedin.service.impl.ExperienceServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ExperienceController {

    private final ExperienceServiceImpl experienceService;

    public ExperienceController(ExperienceServiceImpl experienceService) {
        this.experienceService = experienceService;
    }

    @GetMapping("/new/experience")
    public String showExperienceForm(Model model) {
        model.addAttribute("experience", new Experience());
        return "add-experience";
    }

    @PostMapping("/add/experience/{userId}")
    public String addExperience(@PathVariable("userId") Long userId, Experience experience) {
        experienceService.addExperience(userId, experience);
        return "redirect:/profile/" + userId;
    }
}
