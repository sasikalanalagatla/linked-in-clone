package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Project;
import com.org.linkedin.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/add/project/{userId}")
    public String showAddProjectForm(@PathVariable Long userId, Model model) {
        if (userId == null) throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        model.addAttribute("project", new Project());
        model.addAttribute("userId", userId);
        model.addAttribute("skillsString", "");
        return "add-project";
    }

    @GetMapping("/new/project/{userId}")
    public String showNewProjectForm(@PathVariable Long userId, Model model) {
        if (userId == null) throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
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
        if (userId == null) throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        if (project == null) throw new CustomException("INVALID_PROJECT", "Project data cannot be null");
        if (result.hasErrors()) {
            model.addAttribute("userId", userId);
            model.addAttribute("skillsString", skillsString);
            throw new CustomException("INVALID_PROJECT_DATA", "Invalid project data");
        }
        projectService.saveProject(userId, project, skillsString, result);
        return "redirect:/profile/" + userId;
    }

    @PostMapping("/new/project/{userId}")
    public String addNewProject(@PathVariable Long userId,
                                @Valid @ModelAttribute("project") Project project,
                                BindingResult result,
                                @RequestParam("skillsString") String skillsString,
                                Model model) {
        if (userId == null) throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        if (project == null) throw new CustomException("INVALID_PROJECT", "Project data cannot be null");
        if (result.hasErrors()) {
            model.addAttribute("userId", userId);
            model.addAttribute("skillsString", skillsString);
            throw new CustomException("INVALID_PROJECT_DATA", "Invalid project data");
        }
        projectService.saveProject(userId, project, skillsString, result);
        return "redirect:/profile/" + userId;
    }

    @GetMapping("/edit/project/{userId}/{projectId}")
    public String showEditProjectForm(@PathVariable Long userId, @PathVariable Long projectId, Model model) {
        if (userId == null) throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        if (projectId == null) throw new CustomException("INVALID_PROJECT_ID", "Project ID cannot be null");
        Project project = projectService.getProjectById(projectId);
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
        if (userId == null) throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        if (projectId == null) throw new CustomException("INVALID_PROJECT_ID", "Project ID cannot be null");
        if (project == null) throw new CustomException("INVALID_PROJECT", "Project data cannot be null");
        if (result.hasErrors()) {
            model.addAttribute("userId", userId);
            model.addAttribute("skillsString", skillsString);
            throw new CustomException("INVALID_PROJECT_DATA", "Invalid project data");
        }
        projectService.updateProject(userId, projectId, project, skillsString, result);
        return "redirect:/profile/" + userId;
    }

    @PostMapping("/delete/project/{userId}/{projectId}")
    public String deleteProject(@PathVariable Long userId, @PathVariable Long projectId) {
        if (userId == null) throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        if (projectId == null) throw new CustomException("INVALID_PROJECT_ID", "Project ID cannot be null");
        projectService.deleteProject(projectId);
        return "redirect:/profile/" + userId;
    }
}