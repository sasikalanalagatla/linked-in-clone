package com.org.linkedin.service;

import com.org.linkedin.model.Project;
import org.springframework.validation.BindingResult;

public interface ProjectService {

    void saveProject(Long userId, Project project, String skillsString, BindingResult result);
    void updateProject(Long userId, Long projectId, Project project, String skillsString, BindingResult result);
    Project getProjectById(Long projectId);
    String getSkillsStringForProject(Long projectId);
    void deleteProject(Long projectId);
}