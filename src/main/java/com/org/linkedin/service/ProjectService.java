package com.org.linkedin.service;

import com.org.linkedin.model.Project;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface ProjectService {
    void saveProject(Long userId, Project project, String skillsString, BindingResult result);
    void updateProject(Long userId, Long projectId, Project project, String skillsString, BindingResult result);
    Project getProjectById(Long projectId);
    String getSkillsStringForProject(Long projectId);
    List<Project> getProjectsByUserId(Long userId);
    void deleteProject(Long projectId);
}