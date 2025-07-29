package com.org.linkedin.controller;

import com.org.linkedin.model.Education;
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
        return educationService.showEducationForm(userId, model, userService);
    }

    @PostMapping("/add/education/{userId}")
    public String addEducation(@PathVariable("userId") Long userId,
                               @ModelAttribute Education education,
                               Model model) {
        return educationService.addEducation(userId, education, model, userService);
    }

    @GetMapping("/edit/education/{educationId}")
    public String showEditEducationForm(@PathVariable("educationId") Long educationId, Model model) {
        return educationService.showEditEducationForm(educationId, model, userService);
    }

    @PostMapping("/update/education/{educationId}")
    public String updateEducation(@PathVariable("educationId") Long educationId,
                                  @ModelAttribute Education education,
                                  Model model) {
        return educationService.updateEducation(educationId, education, model, userService);
    }

    @PostMapping("/delete/education/{educationId}")
    public String deleteEducation(@PathVariable("educationId") Long educationId, Model model) {
        return educationService.deleteEducation(educationId, model);
    }

    @GetMapping("/delete/education/{educationId}")
    public String deleteEducationGet(@PathVariable("educationId") Long educationId, Model model) {
        return educationService.deleteEducation(educationId, model);
    }
}