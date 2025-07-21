package com.org.linkedin.controller;

import com.org.linkedin.model.Project;
import com.org.linkedin.service.impl.ProjectServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProjectController {

    private final ProjectServiceImpl projectService;

    public ProjectController(ProjectServiceImpl projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/new/project/{userId}")
    public String showProjectForm(Model model,@PathVariable("userId")Long userId) {
        model.addAttribute("project", new Project());
        userId = 2l;
        model.addAttribute("userId", userId);
        return "add-project";
    }

    @PostMapping("/add/project/{userId}")
    public String addProject(@PathVariable Long userId, @ModelAttribute Project project) {
        projectService.addProject(userId, project);
        return "redirect:/profile/" + userId;
    }
}
