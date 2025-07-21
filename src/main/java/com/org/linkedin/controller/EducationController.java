package com.org.linkedin.controller;

import com.org.linkedin.model.Education;
import com.org.linkedin.service.impl.EducationServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EducationController {

    private final EducationServiceImpl educationService;

    public EducationController(EducationServiceImpl educationService) {
        this.educationService = educationService;
    }

    @GetMapping("/new/education")
    public String showEducationForm(Model model,Long userId) {
        model.addAttribute("userId", userId);
        model.addAttribute("education", new Education());
        return "add-education";
    }

    @PostMapping("/add/education/{userId}")
    public String addEducation(@PathVariable("userId")Long userId, Education education){
        educationService.addEducation(2l,education);
        return "redirect:/profile/"+2;
    }
}
