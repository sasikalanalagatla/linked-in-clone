package com.org.linkedin.controller;

import com.org.linkedin.model.Project;
import com.org.linkedin.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Original mapping for /add/project/{userId}
    @GetMapping("/add/project/{userId}")
    public String showAddProjectForm(@PathVariable Long userId, Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("userId", userId);
        model.addAttribute("skillsString", "");
        return "add-project";
    }

    // New mapping for /new/project/{userId} - points to same functionality
    @GetMapping("/new/project/{userId}")
    public String showNewProjectForm(@PathVariable Long userId, Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("userId", userId);
        model.addAttribute("skillsString", "");
        return "add-project";
    }

    @PostMapping("/add/project/{userId}")
    public String addProject(@PathVariable Long userId,
                             @Valid @ModelAttribute("project") Project project,
                             BindingResult result,
                             @RequestParam("skillsString") String skillsString,
                             Model model) {
        projectService.saveProject(userId, project, skillsString, result);
        if (result.hasErrors()) {
            model.addAttribute("userId", userId);
            model.addAttribute("skillsString", skillsString);
            return "add-project";
        }
        return "redirect:/"; // Redirect to home instead of projects
    }

    // Also add POST mapping for /new/project/{userId} to handle form submissions
    @PostMapping("/new/project/{userId}")
    public String addNewProject(@PathVariable Long userId,
                                @Valid @ModelAttribute("project") Project project,
                                BindingResult result,
                                @RequestParam("skillsString") String skillsString,
                                Model model) {
        projectService.saveProject(userId, project, skillsString, result);
        if (result.hasErrors()) {
            model.addAttribute("userId", userId);
            model.addAttribute("skillsString", skillsString);
            return "add-project";
        }
        return "redirect:/"; // Redirect to home instead of projects
    }

    @GetMapping("/edit/project/{userId}/{projectId}")
    public String showEditProjectForm(@PathVariable Long userId, @PathVariable Long projectId, Model model) {
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            return "redirect:/"; // Redirect to home if project not found
        }
        String skillsString = projectService.getSkillsStringForProject(projectId);
        model.addAttribute("project", project);
        model.addAttribute("userId", userId);
        model.addAttribute("skillsString", skillsString);
        return "add-project";
    }

    @PostMapping("/edit/project/{userId}/{projectId}")
    public String editProject(@PathVariable Long userId,
                              @PathVariable Long projectId,
                              @Valid @ModelAttribute("project") Project project,
                              BindingResult result,
                              @RequestParam("skillsString") String skillsString,
                              Model model) {
        projectService.updateProject(userId, projectId, project, skillsString, result);
        if (result.hasErrors()) {
            model.addAttribute("userId", userId);
            model.addAttribute("skillsString", skillsString);
            return "add-project";
        }
        return "redirect:/"; // Redirect to home
    }

    @PostMapping("/delete/project/{userId}/{projectId}")
    public String deleteProject(@PathVariable Long userId, @PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return "redirect:/"; // Redirect to home
    }
}